package br.edu.ifrn.frotaif.dto.abastecimento;

import br.edu.ifrn.frotaif.dto.veiculo.VeiculoResumoResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AbastecimentoResponse(
        Long id,
        VeiculoResumoResponse veiculo,
        LocalDate data,
        BigDecimal litros,
        BigDecimal valorTotal,
        Long kmNoAbastecimento,
        String posto
) {
}
