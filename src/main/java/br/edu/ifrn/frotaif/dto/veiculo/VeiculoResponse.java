package br.edu.ifrn.frotaif.dto.veiculo;

import br.edu.ifrn.frotaif.model.enums.StatusVeiculo;
import br.edu.ifrn.frotaif.model.enums.TipoVeiculo;

public record VeiculoResponse(
        Long id,
        String placa,
        String marca,
        String modelo,
        Integer ano,
        TipoVeiculo tipo,
        Integer capacidadePassageiros,
        Long quilometragemAtual,
        StatusVeiculo status
) {
}
