package br.edu.ifrn.frotaif.dto.auth;

// Mesmos campos da resposta de token do OAuth 2 (access_token, token_type, expires_in),
// para o front-end não mudar quando o token passar a vir do Keycloak
public record TokenResponse(
        String accessToken,
        String tokenType,   // sempre "Bearer": é o prefixo do cabeçalho Authorization
        long expiresIn      // validade em segundos
) {
}
