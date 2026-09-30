package br.edu.ifrn.frotaif.model.enums;

/** Categorias de CNH. A ordem importa: D e E habilitam VAN e ONIBUS (RN08). */
public enum CategoriaCnh {
    B, C, D, E;

    public boolean habilitaVeiculoGrande() {
        return this.compareTo(D) >= 0;
    }
}
