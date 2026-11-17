package br.edu.ifrn.frotaif.mapper;

import br.edu.ifrn.frotaif.dto.usuario.UsuarioResumoResponse;
import br.edu.ifrn.frotaif.model.Usuario;
import org.mapstruct.Mapper;

@Mapper
public interface UsuarioMapper {

    UsuarioResumoResponse toResumo(Usuario usuario);
}
