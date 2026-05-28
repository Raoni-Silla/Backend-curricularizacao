package fema.metodosageis.SistemaMetodosAgeis.service;

import fema.metodosageis.SistemaMetodosAgeis.dto.pergunta.AlternativaDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.pergunta.PerguntaDTO;
import fema.metodosageis.SistemaMetodosAgeis.model.Prova;
import fema.metodosageis.SistemaMetodosAgeis.repository.ProvaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvaService {

    private final ProvaRepository provaRepository;

    @Transactional
    public void ativarProva (Long provaId) {
        Prova prova = provaRepository.findById(provaId).orElseThrow(() -> new RuntimeException("prova não encontrada"));
        prova.setAtiva(true);
    }

    @Transactional
    public void desativarProva (Long provaId) {
        Prova prova = provaRepository.findById(provaId).orElseThrow(() -> new RuntimeException("prova não encontrada"));
        prova.setAtiva(false);
    }

    public List<PerguntaDTO> buscarProvaParaAluno (Long provaId){
        Prova prova = provaRepository.findById(provaId).orElseThrow(() -> new RuntimeException("prova não encontrada"));
        if (!prova.isAtiva()){
            throw new RuntimeException("A prova não está ativa");
        }
        return prova.getPerguntas().stream().map(pergunta -> {

            List<AlternativaDTO> alternativasDto = pergunta.getAlternativas().stream()
                    .map(alt -> new AlternativaDTO(alt.getId(), alt.getDescricao()))
                    .toList();

            return new PerguntaDTO(
                    pergunta.getId(),
                    pergunta.getTitulo(),
                    pergunta.getEnunciado(),
                    alternativasDto
            );
        }).toList();
    }
}
