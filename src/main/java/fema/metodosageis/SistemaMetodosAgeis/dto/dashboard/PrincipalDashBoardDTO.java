package fema.metodosageis.SistemaMetodosAgeis.dto.dashboard;

import java.util.List;

public record PrincipalDashBoardDTO(
        boolean provaAtiva,
        ResumoDashBoardDTO resumo,
        List<ExperienciaDashBoardDTO> experiencias,
        List<ResumoTentativaDTO> tentativas
) {
}
