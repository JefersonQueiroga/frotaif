package br.edu.ifrn.frotaif.mapper;

import br.edu.ifrn.frotaif.dto.motorista.MotoristaRequest;
import br.edu.ifrn.frotaif.dto.motorista.MotoristaResponse;
import br.edu.ifrn.frotaif.dto.motorista.MotoristaResumoResponse;
import br.edu.ifrn.frotaif.model.Motorista;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper
public interface MotoristaMapper {

    // ativo começa true; o vínculo com o login (usuario) entra na aula de Security
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Motorista toEntity(MotoristaRequest request);

    MotoristaResponse toResponse(Motorista motorista);

    List<MotoristaResponse> toResponseList(List<Motorista> motoristas);

    MotoristaResumoResponse toResumo(Motorista motorista);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    void atualizar(MotoristaRequest request, @MappingTarget Motorista motorista);
}
