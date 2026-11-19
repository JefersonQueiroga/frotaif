package br.edu.ifrn.frotaif.controller.doc;

import br.edu.ifrn.frotaif.dto.auth.LoginRequest;
import br.edu.ifrn.frotaif.dto.auth.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

// Documentação da API (Swagger) do AuthController — o controller só implementa esta interface
@Tag(name = "Autenticação", description = "Login: troca e-mail e senha por um token JWT")
public interface AuthControllerDoc {

    @Operation(summary = "Faz login e devolve um token JWT",
            description = "Confere e-mail e senha (BCrypt) e devolve um token assinado. Nas demais rotas, envie "
                    + "o cabeçalho Authorization: Bearer <accessToken> (no Swagger: botão Authorize).")
    @SecurityRequirements // rota pública: não exige token
    @ApiResponse(responseCode = "200", description = "Login aceito: token no campo accessToken")
    @ApiResponse(responseCode = "400", description = "E-mail ou senha ausentes, ou e-mail inválido")
    @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos, ou usuário inativo")
    TokenResponse login(@RequestBody(content = @Content(examples = @ExampleObject(
            value = "{\"email\": \"gestor@ifrn.edu.br\", \"senha\": \"123456\"}"))) LoginRequest request);
}
