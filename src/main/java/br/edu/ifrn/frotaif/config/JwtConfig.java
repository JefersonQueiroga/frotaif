package br.edu.ifrn.frotaif.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

// O FrotaIF faz os dois papéis: EMITE o token no login (JwtEncoder) e CONFERE o token
// nas demais requisições (JwtDecoder). Os dois usam a mesma chave secreta (HS256).
// Quando entrar o Keycloak, o JwtEncoder sai daqui e o JwtDecoder passa a usar a chave pública dele.
@Configuration
public class JwtConfig {

    @Bean
    public SecretKey chaveJwt(JwtProperties jwtProperties) {
        byte[] bytes = jwtProperties.segredo().getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("frota.jwt.segredo precisa ter pelo menos 32 caracteres (HS256)");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    // Assina o token no login
    @Bean
    public JwtEncoder jwtEncoder(SecretKey chaveJwt) {
        return NimbusJwtEncoder.withSecretKey(chaveJwt).algorithm(MacAlgorithm.HS256).build();
    }

    // Confere o token a cada requisição: assinatura, validade (exp) e emissor (iss)
    @Bean
    public JwtDecoder jwtDecoder(SecretKey chaveJwt, JwtProperties jwtProperties) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(chaveJwt).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(jwtProperties.emissor()));
        return decoder;
    }
}
