package br.edu.ifrn.frotaif.dto.motorista;

import br.edu.ifrn.frotaif.model.enums.CategoriaCnh;

// Versão enxuta, usada dentro da viagem: não expõe CPF nem registro da CNH
public record MotoristaResumoResponse(Long id, String nome, CategoriaCnh categoriaCnh) {
}
