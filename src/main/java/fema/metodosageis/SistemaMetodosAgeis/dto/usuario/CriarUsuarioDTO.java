package fema.metodosageis.SistemaMetodosAgeis.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarUsuarioDTO(
        @NotBlank
        String nome,
        @NotNull
        int idade,
        @NotBlank
        String sexo,
        @NotBlank
        String escolaridade,
        @NotBlank
        String experiencia
) {}