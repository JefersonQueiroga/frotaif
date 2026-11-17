package br.edu.ifrn.frotaif.dto.usuario;

import br.edu.ifrn.frotaif.model.enums.Papel;

// Nunca expõe senha nem e-mail: o DTO decide o que sai na API
public record UsuarioResumoResponse(Long id, String nome, Papel papel) {
}
