package br.edu.ifrn.frotaif.dto.viagem;

// Corpo de PATCH /api/viagens/{id}/aprovar — a obrigatoriedade é verificada no service (RF06)
public record AprovarViagemRequest(Long veiculoId, Long motoristaId) {
}
