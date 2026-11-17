package br.edu.ifrn.frotaif.dto.manutencao;

import br.edu.ifrn.frotaif.dto.veiculo.VeiculoResumoResponse;
import br.edu.ifrn.frotaif.model.enums.TipoManutencao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ManutencaoResponse(
        Long id,
        VeiculoResumoResponse veiculo,
        TipoManutencao tipo,
        String descricao,
        LocalDate dataInicio,
        LocalDate dataPrevistaFim,
        LocalDate dataFim,
        BigDecimal custo,
        boolean aberta      // vem de Manutencao.isAberta()
) {
}
