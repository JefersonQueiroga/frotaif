package br.edu.ifrn.frotaif.dto.manutencao;

import java.math.BigDecimal;
import java.time.LocalDate;

// Corpo de PATCH /api/manutencoes/{id}/encerrar — validado no service (RN13)
public record EncerrarManutencaoRequest(LocalDate dataFim, BigDecimal custo) {
}
