package fema.metodosageis.SistemaMetodosAgeis.dto.usuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        int acertos
) {}