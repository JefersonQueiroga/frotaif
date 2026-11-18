package br.edu.ifrn.frotaif.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

// Substitui a configuração padrão do Spring Boot (usuário "user" com senha no log).
// Quem são os usuários: UsuarioDetailsService (banco). Como a senha foi guardada: PasswordEncoder (BCrypt).
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF protege login por cookie; a API recebe a credencial no cabeçalho a cada requisição
                .csrf(csrf -> csrf.disable())
                // A API não cria sessão: cada requisição traz a própria credencial
                .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
                // Console do H2 abre em <frame>: libera frames da mesma origem
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                // Regras por papel (tabela de endpoints dos requisitos).
                // A ordem importa: vale a PRIMEIRA regra que casar, então as específicas vêm antes.
                .authorizeHttpRequests(auth -> auth
                        // Documentação (Swagger) e console do H2 (só existe no perfil padrão, com H2 em memória)
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                                "/h2-console/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/veiculos").authenticated()
                        .requestMatchers("/api/veiculos/**", "/api/motoristas/**",
                                "/api/manutencoes/**").hasRole("GESTOR")

                        .requestMatchers(HttpMethod.POST, "/api/viagens").hasRole("SERVIDOR")
                        .requestMatchers("/api/viagens/*/aprovar",
                                "/api/viagens/*/rejeitar").hasRole("GESTOR")
                        .requestMatchers("/api/viagens/*/iniciar",
                                "/api/viagens/*/concluir").hasRole("MOTORISTA")
                        .requestMatchers("/api/viagens/*/cancelar").hasAnyRole("SERVIDOR", "GESTOR")

                        // Regra geral por último: o que não foi listado exige pelo menos login
                        .anyRequest().authenticated())
                // Usuário e senha no cabeçalho Authorization (provisório: depois entra o JWT)
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    // Com este bean, todo o framework passa a comparar senhas com BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
