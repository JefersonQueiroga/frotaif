package br.edu.ifrn.frotaif.dto.viagem;

// Corpo de rejeitar e cancelar — a obrigatoriedade do motivo é verificada no service (RN14)
public record MotivoRequest(String motivo) {
}
