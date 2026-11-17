package br.edu.ifrn.frotaif.dto.motorista;

import br.edu.ifrn.frotaif.model.enums.CategoriaCnh;

import java.time.LocalDate;

public record MotoristaResponse(
        Long id,
        String nome,
        String cpf,
        String registroCnh,
        CategoriaCnh categoriaCnh,
        LocalDate validadeCnh,
        boolean ativo
) {
}
