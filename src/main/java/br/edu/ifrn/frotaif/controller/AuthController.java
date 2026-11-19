package br.edu.ifrn.frotaif.controller;

import br.edu.ifrn.frotaif.controller.doc.AuthControllerDoc;
import br.edu.ifrn.frotaif.dto.auth.LoginRequest;
import br.edu.ifrn.frotaif.dto.auth.TokenResponse;
import br.edu.ifrn.frotaif.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDoc {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    // Corpo: {"email": "gestor@ifrn.edu.br", "senha": "123456"}
    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest request) {
        return tokenService.login(request);
    }
}
