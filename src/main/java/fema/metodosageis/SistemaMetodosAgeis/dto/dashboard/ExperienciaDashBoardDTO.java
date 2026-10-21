package fema.metodosageis.SistemaMetodosAgeis.dto.dashboard;

import fema.metodosageis.SistemaMetodosAgeis.enums.Experiencia;

public record ExperienciaDashBoardDTO(
        Experiencia exp,
        Long qntdParticipantes,
        Long mediaAcertos
) {
}
