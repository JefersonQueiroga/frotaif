package br.edu.ifrn.frotaif.dto.manutencao;

import br.edu.ifrn.frotaif.model.enums.TipoManutencao;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AbrirManutencaoRequest(
        @NotNull TipoManutencao tipo,
        String descricao,
        @NotNull LocalDate dataInicio,
        @NotNull LocalDate dataPrevistaFim
) {
}
