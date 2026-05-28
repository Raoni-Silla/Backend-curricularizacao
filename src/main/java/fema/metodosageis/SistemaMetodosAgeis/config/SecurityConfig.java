package fema.metodosageis.SistemaMetodosAgeis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Só quem tem ROLE_ADMIN entra aqui
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Alunos e Admins entram aqui
                        .requestMatchers("/api/provas/**", "/api/tentativas/**").hasAnyRole("ALUNO", "ADMIN")

                        // Cadastro inicial livre
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults()); // Vai abrir aquela janelinha de login no navegador

        return http.build();
    }
}