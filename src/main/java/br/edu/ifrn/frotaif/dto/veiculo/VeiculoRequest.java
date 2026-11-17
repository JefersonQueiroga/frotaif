package br.edu.ifrn.frotaif.dto.veiculo;

import br.edu.ifrn.frotaif.model.enums.TipoVeiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

// O que o cliente PODE enviar: sem id e sem status (definidos pelo sistema)
public record VeiculoRequest(

        // Formato Mercosul AAA0A00 (RN01)
        @NotBlank
        @Pattern(regexp = "[A-Z]{3}[0-9][A-Z][0-9]{2}", message = "placa deve estar no formato Mercosul AAA0A00")
        String placa,

        @NotBlank String marca,
        @NotBlank String modelo,
        Integer ano,
        @NotNull TipoVeiculo tipo,
        @NotNull @Positive Integer capacidadePassageiros,

        // Só é usado no cadastro; na edição a quilometragem muda pelas viagens/abastecimentos
        @PositiveOrZero Long quilometragemAtual
) {
}
