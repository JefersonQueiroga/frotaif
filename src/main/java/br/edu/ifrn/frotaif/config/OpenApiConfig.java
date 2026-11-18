package br.edu.ifrn.frotaif.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// Cabeçalho da documentação. Os endpoints são descobertos sozinhos a partir dos controllers.
// Swagger UI: http://localhost:8080/swagger-ui.html · JSON: http://localhost:8080/v3/api-docs
// @SecurityScheme cria o botão "Authorize" do Swagger: informe e-mail e senha de um usuário do data.sql
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "FrotaIF API",
        version = "v1",
        description = "Gestão da frota de veículos do campus (DSC/IFRN). "
                + "Login via HTTP Basic: e-mail e senha de um usuário cadastrado (ex.: gestor@ifrn.edu.br / 123456)."
), security = @SecurityRequirement(name = "basicAuth"))
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
public class OpenApiConfig {
}
