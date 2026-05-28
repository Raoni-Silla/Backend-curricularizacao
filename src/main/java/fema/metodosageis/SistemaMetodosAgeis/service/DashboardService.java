package fema.metodosageis.SistemaMetodosAgeis.service;

import fema.metodosageis.SistemaMetodosAgeis.dto.dashboard.DashboardDTO;
import fema.metodosageis.SistemaMetodosAgeis.enums.StatusProva;
import fema.metodosageis.SistemaMetodosAgeis.repository.RespostaRepository;
import fema.metodosageis.SistemaMetodosAgeis.repository.TentativaProvaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final RespostaRepository respostaRepository;
    private final TentativaProvaRepository tentativaRepository;

    public DashboardDTO calcularDadosGerais() {
        DashboardDTO dto = new DashboardDTO();

        // 1. Média Geral
        long totalRespostas = respostaRepository.count();
        long totalAcertos = respostaRepository.countByCorreta(true);
        dto.setMediaGeralAcertos(totalRespostas > 0 ? (totalAcertos * 100.0) / totalRespostas : 0.0);

        // 2. Abandono
        dto.setTotalAbandono(tentativaRepository.countByStatus(StatusProva.EXPIRADA));

        // 3. Pergunta Vilã
        dto.setPerguntaVila(respostaRepository.buscarPerguntaVila());

        // 4. Mapas para os Gráficos
        dto.setAcertosPorEscolaridade(converterParaMap(respostaRepository.buscarAcertosPorEscolaridade()));
        dto.setAcertosPorSexo(converterParaMap(respostaRepository.buscarAcertosPorSexo()));

        // 5. Ranking
        dto.setTopAlunos(respostaRepository.buscarTop5Alunos());

        return dto;
    }

    private Map<String, Double> converterParaMap(List<Object[]> resultados) {
        return resultados.stream().collect(Collectors.toMap(
                row -> row[0].toString(), //chave
                row -> Double.valueOf(row[1].toString()) //valor
        ));
    }
}
