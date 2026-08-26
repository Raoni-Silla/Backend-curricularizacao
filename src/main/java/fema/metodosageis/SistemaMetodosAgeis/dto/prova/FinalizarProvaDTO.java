package fema.metodosageis.SistemaMetodosAgeis.dto.prova;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record FinalizarProvaDTO(
        @NotNull
        Long idTentativa,
        @NotEmpty
        Map<Long, Long> respostas
) {
}
