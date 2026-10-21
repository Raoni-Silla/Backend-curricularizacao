package fema.metodosageis.SistemaMetodosAgeis.dto.dashboard;

public record ResumoDashBoardDTO(

        Long totalParticipantes,
        Long qntdEmAndamento,
        Long qntdFinalizada,
        Long qntdExpiradas,
        Long mediaAcertos
) {
}
