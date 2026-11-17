package br.edu.ifrn.frotaif.dto.abastecimento;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

// Sem veículo: ele vem da URL (/api/veiculos/{veiculoId}/abastecimentos)
public record AbastecimentoRequest(
        @NotNull @PastOrPresent LocalDate data,
        @NotNull @Positive BigDecimal litros,
        @NotNull @Positive BigDecimal valorTotal,
        @NotNull @PositiveOrZero Long kmNoAbastecimento,
        String posto
) {
}
