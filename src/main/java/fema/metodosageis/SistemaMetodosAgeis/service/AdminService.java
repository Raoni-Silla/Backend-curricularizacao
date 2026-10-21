package fema.metodosageis.SistemaMetodosAgeis.service;

import fema.metodosageis.SistemaMetodosAgeis.config.JwtService;
import fema.metodosageis.SistemaMetodosAgeis.dto.Admin.LoginAdminRequest;
import fema.metodosageis.SistemaMetodosAgeis.dto.Admin.LoginAdminResponseDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.dashboard.ExperienciaDashBoardDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.dashboard.PrincipalDashBoardDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.dashboard.ResumoDashBoardDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.dashboard.ResumoTentativaDTO;
import fema.metodosageis.SistemaMetodosAgeis.enums.Experiencia;
import fema.metodosageis.SistemaMetodosAgeis.enums.StatusProva;
import fema.metodosageis.SistemaMetodosAgeis.enums.TipoUsuario;
import fema.metodosageis.SistemaMetodosAgeis.model.Prova;
import fema.metodosageis.SistemaMetodosAgeis.model.TentativaProva;
import fema.metodosageis.SistemaMetodosAgeis.model.Usuario;
import fema.metodosageis.SistemaMetodosAgeis.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final ProvaRepository provaRepository;
    private final PerguntaRepository perguntaRepository;
    private final TentativaProvaRepository  tentativaProvaRepository;
    private final RespostaRepository respostaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginAdminResponseDTO autenticar(LoginAdminRequest dto) {
        Usuario usuario = usuarioRepository.findByNomeAndTipoUsuario(dto.nome(), TipoUsuario.ADMIN).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse usuario"));
        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }
        String token = jwtService.gerarTokenDefinitivo(usuario);
        return new LoginAdminResponseDTO(token);
    }



    @Transactional
    public PrincipalDashBoardDTO carregarDashboard () {
        Prova prova = provaRepository.findById(1L).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar essa prova"));
        List<TentativaProva> tentativasProva = tentativaProvaRepository.findByProvaAndUsuario_TipoUsuario(prova, TipoUsuario.ALUNO);
        Integer questoesTotaisDaProva = Math.toIntExact(perguntaRepository.countByProva(prova));
        List<ResumoTentativaDTO> resumoTentativa = tentativasProva.stream().map(t -> {

            Integer acertos = null;


            if (t.getStatus() == StatusProva.FINALIZADA){
               acertos = Math.toIntExact(respostaRepository.countByTentativaProvaAndCorretaIsTrue(t));
            }

            return new ResumoTentativaDTO(t.getId(), t.getUsuario().getNome(), t.getUsuario().getIdade(), t.getUsuario().getEscolaridade(), t.getUsuario().getExperiencia(), t.getStatus(),t.getInicio(),t.getFim(), acertos, questoesTotaisDaProva);
        }).toList();
        ResumoDashBoardDTO  resumoDashBoardDTO = new ResumoDashBoardDTO((long) resumoTentativa.size(), resumoTentativa.stream().filter(r -> r.status() == StatusProva.EM_ANDAMENTO).count(),resumoTentativa.stream().filter(r -> r.status() == StatusProva.FINALIZADA).count(),resumoTentativa.stream().filter(r -> r.status() == StatusProva.EXPIRADA).count(), calcularMediaEmPorcentagem(resumoTentativa));
        List<ExperienciaDashBoardDTO> experienciaDashBoardDTOS = Arrays.stream(Experiencia.values()).map(e -> {
            List<ResumoTentativaDTO> listaFiltrada = resumoTentativa.stream().filter(r -> e == r.experiencia()).toList();
            return new ExperienciaDashBoardDTO(e, (long) listaFiltrada.size(),calcularMediaEmPorcentagem(listaFiltrada));
        }).toList();
        return new PrincipalDashBoardDTO(
            prova.isAtiva(),
            resumoDashBoardDTO,
            experienciaDashBoardDTOS,
            resumoTentativa
        );
    }


    private Long calcularMediaEmPorcentagem (List<ResumoTentativaDTO> resumoTentativa){
        List<ResumoTentativaDTO> alunosQueFinalizaramProva =  resumoTentativa.stream().filter(t -> t.acertos() != null).toList();
        if (alunosQueFinalizaramProva.isEmpty()){
            return null;
        }
        double somaDosAcertos = alunosQueFinalizaramProva.stream().map(a -> {
            double valorPorAcerto = 100.0 / a.totalQuestoes();
            return a.acertos() * valorPorAcerto;
        }).reduce(0.0, Double::sum);
        return  Math.round(somaDosAcertos / alunosQueFinalizaramProva.size());
    }


    @Transactional
    public PrincipalDashBoardDTO desativarProva () {
        Prova prova = provaRepository.findById(1L).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar essa prova"));
        if (!prova.isAtiva()){
            throw new IllegalArgumentException("essa prova já está desativada");
        }
        prova.setAtiva(false);
        List<TentativaProva> tentativaProvaEmAndamento = tentativaProvaRepository.findByProvaAndStatus(prova, StatusProva.EM_ANDAMENTO);
        LocalDateTime agora = LocalDateTime.now();
        tentativaProvaEmAndamento.forEach(t -> {
            t.setStatus(StatusProva.EXPIRADA);
            t.setFim(agora);
        });
        return carregarDashboard();
    }


    @Transactional
    public PrincipalDashBoardDTO ativarProva () {
        Prova prova = provaRepository.findById(1L).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar essa prova"));
        if (prova.isAtiva()){
            throw new IllegalArgumentException("essa prova já está ativada");
        }
        prova.setAtiva(true);
        return carregarDashboard();
    }

}
