package br.edu.ifrn.frotaif.service;

import br.edu.ifrn.frotaif.config.JwtProperties;
import br.edu.ifrn.frotaif.dto.auth.LoginRequest;
import br.edu.ifrn.frotaif.dto.auth.TokenResponse;
import br.edu.ifrn.frotaif.exception.CredenciaisInvalidasException;
import br.edu.ifrn.frotaif.model.Usuario;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

// Login: confere e-mail e senha UMA vez e devolve um token assinado
@Service
public class TokenService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public TokenService(AuthenticationManager authenticationManager,
                        JwtEncoder jwtEncoder,
                        JwtProperties jwtProperties) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    public TokenResponse login(LoginRequest request) {
        // O mesmo caminho do HTTP Basic: AuthenticationManager → UsuarioDetailsService → BCrypt.
        // A diferença é que agora acontece só aqui, e não em toda requisição.
        Authentication autenticacao;
        try {
            autenticacao = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.senha()));
        } catch (AuthenticationException e) {
            throw new CredenciaisInvalidasException("E-mail ou senha inválidos");
        }
        Usuario usuario = (Usuario) autenticacao.getPrincipal();

        Instant agora = Instant.now();
        long validadeSegundos = jwtProperties.expiracaoMinutos() * 60;

        // Claims: o conteúdo do token. Qualquer um consegue LER (é só Base64), mas ninguém consegue
        // ALTERAR sem invalidar a assinatura. Por isso nunca coloque a senha aqui.
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.emissor())                // iss: quem emitiu
                .subject(usuario.getEmail())                   // sub: de quem é o token
                .issuedAt(agora)                               // iat: quando foi emitido
                .expiresAt(agora.plusSeconds(validadeSegundos)) // exp: até quando vale
                .claim("nome", usuario.getNome())
                .claim("papel", usuario.getPapel().name())     // informativo para o front-end
                .build();

        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(cabecalho, claims)).getTokenValue();
        return new TokenResponse(token, "Bearer", validadeSegundos);
    }
}
