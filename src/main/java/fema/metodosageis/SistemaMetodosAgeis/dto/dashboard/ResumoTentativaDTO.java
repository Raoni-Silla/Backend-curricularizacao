package fema.metodosageis.SistemaMetodosAgeis.dto.dashboard;

import fema.metodosageis.SistemaMetodosAgeis.enums.Escolaridade;
import fema.metodosageis.SistemaMetodosAgeis.enums.Experiencia;
import fema.metodosageis.SistemaMetodosAgeis.enums.StatusProva;

import java.time.LocalDateTime;

public record ResumoTentativaDTO(
        Long idTentativa,
        String nome,
        int idade,
        Escolaridade escolaridade,
        Experiencia experiencia,
        StatusProva status,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        Integer acertos,
        Integer totalQuestoes
) {
}
