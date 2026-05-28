package fema.metodosageis.SistemaMetodosAgeis.service;

import fema.metodosageis.SistemaMetodosAgeis.dto.prova.ResultadoProvaDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.prova.IniciarProvaDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.usuario.CriarUsuarioDTO;
import fema.metodosageis.SistemaMetodosAgeis.enums.StatusProva;
import fema.metodosageis.SistemaMetodosAgeis.model.*;
import fema.metodosageis.SistemaMetodosAgeis.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TentativaProvaService {

    private final TentativaProvaRepository tentativaProvaRepository;
    private final ProvaRepository provaRepository;
    private final UsuarioService usuarioService;
    private final RespostaRepository respostaRepository;
    private final AlternativaRepository alternativaRepository;
    private final PerguntaRepository perguntaRepository;

    private boolean isExpirada (TentativaProva tentativa){
        return LocalDateTime.now().isAfter(
                tentativa.getInicio().plusMinutes(
                        tentativa.getProva().getDuracaoMinutos()
                )
        );
    }

    private CriarUsuarioDTO mapperUsuarioDto (IniciarProvaDTO provaDto){
        return new CriarUsuarioDTO(provaDto.nome(),provaDto.idade(),provaDto.sexo(),provaDto.escolaridade());
    }

    @Transactional
    public Long iniciarProva (IniciarProvaDTO provaDto){
        Prova prova = provaRepository.findById(provaDto.provaId()).orElseThrow(() -> new RuntimeException("não encontramos a prova"));
        if (prova.isAtiva()){
            Usuario usuarioCriado = usuarioService.criarUsuario(mapperUsuarioDto(provaDto));
            TentativaProva tentativaProva = new TentativaProva();
            tentativaProva.setInicio(LocalDateTime.now());
            tentativaProva.setStatus(StatusProva.EM_ANDAMENTO);
            tentativaProva.setUsuario(usuarioCriado);
            tentativaProva.setProva(prova);
            TentativaProva save = tentativaProvaRepository.save(tentativaProva);
            return save.getId();
        }else {
            throw new RuntimeException("Prova não está ativa");
        }
    }

    @Transactional
    public ResultadoProvaDTO finalizarProva(Long tentativaID) {

        TentativaProva tentativa = tentativaProvaRepository.findById(tentativaID)
                .orElseThrow(() -> new RuntimeException("Tentativa não encontrada"));

        if (tentativa.getStatus() != StatusProva.EM_ANDAMENTO) {
            throw new RuntimeException("Prova já foi finalizada");
        }


        /*a prova já está expirada?*/
        boolean expirou = isExpirada(tentativa);

        /*se sim, status == expirada senão status finalizada*/
        tentativa.setStatus(expirou ? StatusProva.EXPIRADA : StatusProva.FINALIZADA);

        tentativa.setFim(LocalDateTime.now());

        List<Resposta> respostas = respostaRepository.findBytentativaProvaId(tentativaID);

        long total = tentativa.getProva().getPerguntas().size();
        long corretas = respostas.stream().filter(Resposta::isCorreta).count();
        long incorretas = total - corretas;

        tentativaProvaRepository.save(tentativa);

        return new ResultadoProvaDTO(
                total,
                /*se não tiver nenhuma resposta, retorna 0 senão calcula*/
                total == 0 ? 0 : (double) incorretas / total * 100,
                total == 0 ? 0 : (double) corretas / total * 100
        );
    }

    @Transactional
    public void salvarResposta (Long tentativaID, Long perguntaID, Long alternativaID){

        // 1. Validações que você já fez (Tentativa existe? Está em andamento? Expirou?)
        TentativaProva tentativa = tentativaProvaRepository.findById(tentativaID)
                .orElseThrow(() -> new RuntimeException("Tentativa não encontrada"));

        if (tentativa.getStatus() != StatusProva.EM_ANDAMENTO) {
            throw new RuntimeException("Prova já foi finalizada");
        }

        if (isExpirada(tentativa)) {
            tentativa.setStatus(StatusProva.EXPIRADA); // Opcional: já atualiza o status
            tentativaProvaRepository.save(tentativa);
            throw new RuntimeException("Tempo esgotado!");
        }

        // 2. Busca a alternativa escolhida para saber se é a correta
        Alternativa alternativa = alternativaRepository.findById(alternativaID)
                .orElseThrow(() -> new RuntimeException("Alternativa não encontrada"));

        // 3. Busca se o usuário já respondeu essa pergunta antes (para não duplicar)
        Resposta resposta = respostaRepository
                .findByTentativaProvaIdAndPerguntaId(tentativaID, perguntaID)
                .orElse(new Resposta()); // Se não achar, cria uma nova


        resposta.setTentativaProva(tentativa);
        resposta.setPergunta(perguntaRepository.findById(perguntaID).orElseThrow(() -> new RuntimeException("Pergunta não encontrada"))); // ou carrega do banco
        resposta.setAlternativa(alternativa);
        resposta.setCorreta(alternativa.isCorreta()); // O segredo está aqui!
        resposta.setRespondidoEm(LocalDateTime.now());

        respostaRepository.save(resposta);
    }


}
