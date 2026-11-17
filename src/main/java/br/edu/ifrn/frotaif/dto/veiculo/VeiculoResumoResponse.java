package br.edu.ifrn.frotaif.dto.veiculo;

// Versão enxuta, usada dentro de outras respostas (viagem, abastecimento, manutenção)
public record VeiculoResumoResponse(Long id, String placa, String modelo) {
}
