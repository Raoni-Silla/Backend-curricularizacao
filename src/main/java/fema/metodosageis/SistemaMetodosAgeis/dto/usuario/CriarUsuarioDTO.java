package fema.metodosageis.SistemaMetodosAgeis.dto.usuario;

public record CriarUsuarioDTO(
        String nome,
        int idade,
        String sexo,
        String escolaridade
) {}