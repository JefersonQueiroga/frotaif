package br.edu.ifrn.frotaif.dto.viagem;

// Corpo de PATCH /api/viagens/{id}/concluir — validado no service (RN10)
public record ConcluirViagemRequest(Long kmFinal) {
}
