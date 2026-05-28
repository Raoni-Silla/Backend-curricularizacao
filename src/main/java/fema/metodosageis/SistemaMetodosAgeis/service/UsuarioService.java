package fema.metodosageis.SistemaMetodosAgeis.service;

import fema.metodosageis.SistemaMetodosAgeis.dto.usuario.CriarUsuarioDTO;
import fema.metodosageis.SistemaMetodosAgeis.dto.usuario.UsuarioResponseDTO;
import fema.metodosageis.SistemaMetodosAgeis.enums.Escolaridade;
import fema.metodosageis.SistemaMetodosAgeis.enums.Sexo;
import fema.metodosageis.SistemaMetodosAgeis.model.Usuario;
import fema.metodosageis.SistemaMetodosAgeis.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private void validarDto (CriarUsuarioDTO dto){
        if (dto.nome().isBlank() || dto.idade() < 10 ){
            throw new IllegalArgumentException("Dados Recebidos São Invalidos");
        }

        try {
           Sexo.valueOf(dto.sexo().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Sexo inválido");
        }

        try {
            Escolaridade.valueOf(dto.escolaridade().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Escolaridade inválida");
        }
    }

    @Transactional
    public Usuario criarUsuario (CriarUsuarioDTO dto){
        validarDto (dto);
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setIdade(dto.idade());
        usuario.setSexo(Sexo.valueOf(dto.sexo()));
        usuario.setEscolaridade(Escolaridade.valueOf(dto.escolaridade()));
        return usuarioRepository.save(usuario);
    }
}

