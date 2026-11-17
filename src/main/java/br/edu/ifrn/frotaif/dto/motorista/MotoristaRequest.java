package br.edu.ifrn.frotaif.dto.motorista;

import br.edu.ifrn.frotaif.model.enums.CategoriaCnh;
import br.edu.ifrn.frotaif.validation.CPF;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

// Sem id, ativo e usuario: definidos pelo sistema
public record MotoristaRequest(
        @NotBlank String nome,
        @NotBlank @CPF String cpf,       // somente dígitos (RN03)
        @NotBlank String registroCnh,    // único (RN03)
        @NotNull CategoriaCnh categoriaCnh,
        @NotNull LocalDate validadeCnh
) {
}
