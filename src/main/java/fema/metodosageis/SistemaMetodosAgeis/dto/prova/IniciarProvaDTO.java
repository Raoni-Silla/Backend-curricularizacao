package fema.metodosageis.SistemaMetodosAgeis.dto.prova;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IniciarProvaDTO(
        @NotBlank
        @Size(min = 3)
        String nome,
        @NotNull
        @Min(10)
        int idade,
        @NotBlank
        String sexo,
        @NotBlank
        String escolaridade,
        Long provaId
) {}