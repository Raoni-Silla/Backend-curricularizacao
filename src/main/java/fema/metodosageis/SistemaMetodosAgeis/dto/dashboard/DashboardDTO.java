package fema.metodosageis.SistemaMetodosAgeis.dto.dashboard;

import fema.metodosageis.SistemaMetodosAgeis.dto.usuario.UsuarioCriadoDTO;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;
import java.util.List;

@Getter @Setter
public class DashboardDTO {
    private Double mediaGeralAcertos;
    private Long totalAbandono;
    private String perguntaVila;

    // Para os gráficos de barras no Angular:
    private Map<String, Double> acertosPorEscolaridade;
    private Map<String, Double> acertosPorSexo;

    // Para a lista de ranking:
    private List<UsuarioCriadoDTO> topAlunos;
}