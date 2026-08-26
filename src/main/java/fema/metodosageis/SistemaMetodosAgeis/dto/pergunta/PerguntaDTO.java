package fema.metodosageis.SistemaMetodosAgeis.dto.pergunta;
import java.util.List;

public record PerguntaDTO(
        Long id,
        String titulo,
        String enunciado,
        List<AlternativaDTO> alternativas
) {}