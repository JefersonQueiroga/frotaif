package br.edu.ifrn.frotaif.dto.viagem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

// Veículo, motorista, km e status NÃO estão aqui: o solicitante não define (RF06).
// O solicitante também não: é o usuário logado (t3-security).
public record SolicitarViagemRequest(
        @NotBlank String destino,
        @NotBlank String finalidade,
        @NotNull LocalDateTime saida,
        @NotNull LocalDateTime retornoPrevisto,
        @NotNull @Positive Integer qtdPassageiros
) {
}
