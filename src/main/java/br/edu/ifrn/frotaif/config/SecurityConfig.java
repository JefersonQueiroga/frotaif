package br.edu.ifrn.frotaif.config;

import br.edu.ifrn.frotaif.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

// Substitui a configuração padrão do Spring Boot (usuário "user" com senha no log).
// Login: POST /api/auth/login confere e-mail e senha (UsuarioDetailsService + BCrypt) e devolve um token JWT.
// Demais rotas: o cliente envia "Authorization: Bearer <token>" e o filtro do resource server confere o token.
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UsuarioRepository usuarioRepository) throws Exception {
        http
                // CSRF protege login por cookie; a API recebe o token no cabeçalho a cada requisição
                .csrf(csrf -> csrf.disable())
                // A API não cria sessão: cada requisição traz o próprio token
                .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
                // Console do H2 abre em <frame>: libera frames da mesma origem
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                // Regras por papel (tabela de endpoints dos requisitos).
                // A ordem importa: vale a PRIMEIRA regra que casar, então as específicas vêm antes.
                .authorizeHttpRequests(auth -> auth
                        // Login é público: é aqui que o cliente consegue o token
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        // Página de erro do Spring Boot: deixa a mensagem do erro aparecer também sem login
                        .requestMatchers("/error").permitAll()
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
                // Token JWT no cabeçalho "Authorization: Bearer ..." (no lugar do httpBasic).
                // O JwtDecoder (JwtConfig) confere assinatura e validade; o conversor carrega o Usuario do banco.
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new JwtUsuarioConverter(usuarioRepository))));
        return http.build();
    }

    // Com este bean, todo o framework passa a comparar senhas com BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // O AuthenticationManager que o Spring monta com o UsuarioDetailsService + PasswordEncoder.
    // Exposto como bean para o TokenService conferir e-mail e senha no login.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
