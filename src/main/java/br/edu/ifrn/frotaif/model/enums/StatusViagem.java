package br.edu.ifrn.frotaif.model.enums;

/** Ciclo de vida da viagem — ver seção 5 do docs/REQUISITOS.md. */
public enum StatusViagem {
    SOLICITADA, APROVADA, REJEITADA, EM_ANDAMENTO, CONCLUIDA, CANCELADA;

    // Máquina de estados (RN09): de cada status, para quais é permitido ir
    public boolean podeMudarPara(StatusViagem novo) {
        return switch (this) {
            case SOLICITADA -> novo == APROVADA || novo == REJEITADA || novo == CANCELADA;
            case APROVADA -> novo == EM_ANDAMENTO || novo == CANCELADA;
            case EM_ANDAMENTO -> novo == CONCLUIDA;
            case REJEITADA, CONCLUIDA, CANCELADA -> false; // estados finais
        };
    }
}
