package br.edu.ifrn.frotaif.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Parâmetros do token JWT lidos do application.yml (prefixo "frota.jwt").
 * Ex.: frota.jwt.expiracao-minutos → expiracaoMinutos
 */
@ConfigurationProperties(prefix = "frota.jwt")
public record JwtProperties(
        String segredo,                            // chave que assina o token (HS256: no mínimo 32 caracteres)
        @DefaultValue("60") long expiracaoMinutos, // validade do token
        @DefaultValue("frotaif") String emissor    // claim "iss": quem emitiu o token
) {
}
