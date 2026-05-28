package fema.metodosageis.SistemaMetodosAgeis.service;

import fema.metodosageis.SistemaMetodosAgeis.model.Usuario;
import fema.metodosageis.SistemaMetodosAgeis.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String nome) throws UsernameNotFoundException {
        // Aqui buscamos pelo Nome (ou você pode mudar para Email depois)
        Usuario usuario = usuarioRepository.findByNome(nome)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + nome));

        // Aqui entregamos o crachá (ROLE_) para o Spring
        return new User(
                usuario.getNome(),
                "{noop}1234", // O "{noop}" diz que a senha não tem criptografia por enquanto (pra facilitar sua vida)
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getTipo().name()))
        );
    }
}