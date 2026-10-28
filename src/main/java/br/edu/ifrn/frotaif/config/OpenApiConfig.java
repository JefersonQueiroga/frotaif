package br.edu.ifrn.frotaif.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

// Cabeçalho da documentação. Os endpoints são descobertos sozinhos a partir dos controllers.
// Swagger UI: http://localhost:8080/swagger-ui.html · JSON: http://localhost:8080/v3/api-docs
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "FrotaIF API",
        version = "v1",
        description = "Gestão da frota de veículos do campus (DSC/IFRN). "
                + "Até a aula de Security, o usuário que age é informado no header X-Usuario-Id."
))
public class OpenApiConfig {
}
