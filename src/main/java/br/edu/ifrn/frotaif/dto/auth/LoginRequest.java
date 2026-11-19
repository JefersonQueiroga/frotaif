package br.edu.ifrn.frotaif.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// A senha só viaja aqui, uma vez. Nas demais requisições vai o token.
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String senha
) {
}
