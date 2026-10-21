package fema.metodosageis.SistemaMetodosAgeis.service;

import fema.metodosageis.SistemaMetodosAgeis.dto.dashboard.ExperienciaDashBoardDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.dashboard.PrincipalDashBoardDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.dashboard.ResumoDashBoardDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.dashboard.ResumoTentativaDTO;
import fema.metodosageis.SistemaMetodosAgeis.enums.Escolaridade;
import fema.metodosageis.SistemaMetodosAgeis.enums.Experiencia;
import fema.metodosageis.SistemaMetodosAgeis.enums.StatusProva;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private static final String CAMINHO_TEMPLATE = "relatorios/relatorio-prova.jrxml";
    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter FORMATO_GERACAO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm", PT_BR);
    private static final DateTimeFormatter FORMATO_INICIO =
            DateTimeFormatter.ofPattern("dd/MM HH:mm", PT_BR);
    private static final Set<String> CONECTIVOS = Set.of("da", "de", "do", "das", "dos", "e");

    private static final Map<Experiencia, String> EXPERIENCIA_LABEL = Map.of(
            Experiencia.NENHUMA, "Nunca ouviu falar",
            Experiencia.BASICA, "Conhece por cima",
            Experiencia.INTERMEDIARIA, "Estudou na faculdade",
            Experiencia.AVANCADA, "Entende bem",
            Experiencia.PROFISSIONAL, "Usa no trabalho"
    );

    private static final Map<Escolaridade, String> ESCOLARIDADE_LABEL = Map.of(
            Escolaridade.FUNDAMENTAL, "Fundamental",
            Escolaridade.MEDIO, "Médio",
            Escolaridade.SUPERIOR, "Superior"
    );

    private static final Map<StatusProva, String> STATUS_LABEL = Map.of(
            StatusProva.EM_ANDAMENTO, "Em andamento",
            StatusProva.FINALIZADA, "Finalizada",
            StatusProva.EXPIRADA, "Expirada"
    );

    private final AdminService adminService;

    private JasperReport templateCompilado;

    public byte[] gerarRelatorioProva() {
        PrincipalDashBoardDTO dashboard = adminService.carregarDashboard();

        try {
            JasperPrint relatorio = JasperFillManager.fillReport(
                    obterTemplate(),
                    montarParametros(dashboard),
                    new JRMapCollectionDataSource(montarLinhasTentativas(dashboard.tentativas()))
            );
            return JasperExportManager.exportReportToPdf(relatorio);
        } catch (JRException e) {
            throw new IllegalStateException("Não foi possível gerar o relatório em PDF", e);
        }
    }

    private Map<String, Object> montarParametros(PrincipalDashBoardDTO dashboard) {
        ResumoDashBoardDTO resumo = dashboard.resumo();

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("GERADO_EM", ZonedDateTime.now(FUSO).format(FORMATO_GERACAO));
        parametros.put("PROVA_ATIVA", dashboard.provaAtiva());
        parametros.put("TOTAL_PARTICIPANTES", resumo.totalParticipantes());
        parametros.put("EM_ANDAMENTO", resumo.qntdEmAndamento());
        parametros.put("FINALIZADAS", resumo.qntdFinalizada());
        parametros.put("EXPIRADAS", resumo.qntdExpiradas());
        parametros.put("MEDIA_ACERTOS", resumo.mediaAcertos());
        parametros.put("EXPERIENCIAS",
                new JRMapCollectionDataSource(montarLinhasExperiencias(dashboard.experiencias())));
        return parametros;
    }

    private List<Map<String, ?>> montarLinhasTentativas(List<ResumoTentativaDTO> tentativas) {
        return tentativas.stream()
                .<Map<String, ?>>map(t -> {
                    Map<String, Object> linha = new HashMap<>();
                    linha.put("nome", formatarNome(t.nome()));
                    linha.put("idade", t.idade());
                    linha.put("escolaridade", ESCOLARIDADE_LABEL.getOrDefault(t.escolaridade(), "—"));
                    linha.put("experiencia", EXPERIENCIA_LABEL.getOrDefault(t.experiencia(), "—"));
                    linha.put("status", STATUS_LABEL.getOrDefault(t.status(), String.valueOf(t.status())));
                    linha.put("inicio", t.dataHoraInicio() != null ? t.dataHoraInicio().format(FORMATO_INICIO) : "—");
                    linha.put("acertos", t.acertos() != null ? t.acertos() + "/" + t.totalQuestoes() : "—");
                    return linha;
                })
                .toList();
    }

    private List<Map<String, ?>> montarLinhasExperiencias(List<ExperienciaDashBoardDTO> experiencias) {
        return experiencias.stream()
                .<Map<String, ?>>map(e -> {
                    Map<String, Object> linha = new HashMap<>();
                    linha.put("nivel", EXPERIENCIA_LABEL.getOrDefault(e.exp(), String.valueOf(e.exp())));
                    linha.put("quantidade", e.qntdParticipantes());
                    linha.put("media", e.mediaAcertos());
                    return linha;
                })
                .toList();
    }

    private String formatarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return "";
        }
        return Arrays.stream(nome.trim().toLowerCase(PT_BR).split("\\s+"))
                .map(parte -> CONECTIVOS.contains(parte)
                        ? parte
                        : parte.substring(0, 1).toUpperCase(PT_BR) + parte.substring(1))
                .collect(Collectors.joining(" "));
    }

    private synchronized JasperReport obterTemplate() throws JRException {
        if (templateCompilado == null) {
            try (InputStream jrxml = new ClassPathResource(CAMINHO_TEMPLATE).getInputStream()) {
                templateCompilado = JasperCompileManager.compileReport(jrxml);
            } catch (IOException e) {
                throw new JRException("Não foi possível ler o template do relatório", e);
            }
        }
        return templateCompilado;
    }
}