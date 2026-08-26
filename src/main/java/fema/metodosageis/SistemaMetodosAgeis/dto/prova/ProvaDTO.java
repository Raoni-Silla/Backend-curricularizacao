package fema.metodosageis.SistemaMetodosAgeis.dto.prova;

import fema.metodosageis.SistemaMetodosAgeis.dto.pergunta.PerguntaDTO;

import java.util.List;

public record ProvaDTO(
        Long idProva,
        String titulo,
        int duracaoMinutos,
        List<PerguntaDTO> listaPerguntas
) {
}
