package fema.metodosageis.SistemaMetodosAgeis.dto.pergunta;

// Esse import agora vai funcionar porque o arquivo está no lugar certo!
import java.util.List;

public record PerguntaDTO(
        Long id,
        String titulo,
        String enunciado,
        List<AlternativaDTO> alternativas
) {}