package fema.metodosageis.SistemaMetodosAgeis.dto.prova;

public record ResultadoProvaDTO(
        long totalPerguntas,
        double errosPercentil,
        double acertosPercentil
) {
}
