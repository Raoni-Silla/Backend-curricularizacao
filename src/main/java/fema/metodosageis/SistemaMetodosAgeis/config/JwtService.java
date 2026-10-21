package fema.metodosageis.SistemaMetodosAgeis.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import fema.metodosageis.SistemaMetodosAgeis.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class JwtService {

    //é a minha senha secreta usada para assinar o token quando misturado o header e o payload
    @Value("${jwt.secret}")
    private String secretKey;


    //quando o usuario faz login com sucesso, o spring chama esse metodo, entregando o objeto usuario
    public String gerarTokenDefinitivo(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            return JWT.create()
                    .withIssuer("curricularizacao-api") //quem imprimiu esse token?
                    .withSubject(String.valueOf(usuario.getId())) // essa pulseira é de qual usuario?
                    .withClaim("ROLE", usuario.getTipoUsuario().name()) //é uma informação a mais adicionada no token e ele é prestador ou usuario
                    .withExpiresAt(gerarDataExpiracao()) // coloca a data de expiração
                    .sign(algorithm); //assina o token

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar o token JWT", exception);
        }
    }

    //leitor de token, quando o usuario tenta acesar uma rota protegida, ele manda o token, o segurança pega o token e coloca nesse metodo
    public String extrairIdentificador(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            return JWT.require(algorithm)
                    .withIssuer("curricularizacao-api")
                    .build()
                    .verify(token) //primeiro verifica se a assinatura foi feita com a chave secreta,vê se quem emitiu foi realmente minha api, verifica se a assinatura bate e se está valido o token
                    .getSubject();
        }catch (JWTVerificationException exception) {
            // Se a assinatura for falsa, se o token expirou ou se estiver malformado, cai aqui.
            throw new RuntimeException("Token JWT inválido ou expirado", exception);
        }
    }

    public String extrairRole(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            return JWT.require(algorithm)
                    .withIssuer("curricularizacao-api")
                    .build()
                    .verify(token)
                    .getClaim("ROLE")
                    .asString();
        }catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado", exception);
        }
    }

    private Instant gerarDataExpiracao(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
