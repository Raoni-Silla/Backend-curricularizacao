package fema.metodosageis.SistemaMetodosAgeis.config;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.equals("/api/exam/iniciarProva")
                || path.equals("/api/exam/provaAtiva");
    }

    //é aqui que toda a ção acontece toda vez que a request tenta acessar algo
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //primeiro se recupera o token com o metodo abaixo
        var token = recuperarToken(request);

        //se o token for diferente de nulo, ou seja, se usuario possuir um token
        if(token != null){
            try{
                //chama o tokenService pra pegar o id e a role desse token
                var identificadorUsuario = jwtService.extrairIdentificador(token);
                var tipoUsuario = jwtService.extrairRole(token);

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + tipoUsuario));

                // aqui o segurança pega um crachá interno do spring security, anota o id e a role desse usuario no crachá
                var authentication = new UsernamePasswordAuthenticationToken(identificadorUsuario, null, authorities);

                // é a prancheta vip do codigo, o segurança pendura o crachá do usuario nessa prancheta, ao fazer isso, ele avisa pro sistema que esse usuario é valido
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token expirado ou invalido. Faca login novamente.");
                return;
            }
        }

        //depois de toda a checagem o segurança permite a passagem
        filterChain.doFilter(request,response);
    }

    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return null; //o usuario não possui um token
        }
        return authHeader.replace("Bearer ", ""); //tira a palavra bearer e deixa somente o token
        //quando mandamos uma requisição na internet, a req nao vem com um token solto, é mandado dentro de um envelope chamado cabeçalho
        //especificamente no campo de authorization e o padrão web é escrever a palavra bearer (portador) antes do token
        //ficando algo como Bearer xisdJSD o trabalho desse metodo é verficar se tem algo no cabeçalho, se tiver retira o token
    }
}
