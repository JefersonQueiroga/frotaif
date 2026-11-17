package br.edu.ifrn.frotaif.mapper;

import br.edu.ifrn.frotaif.dto.abastecimento.AbastecimentoRequest;
import br.edu.ifrn.frotaif.dto.abastecimento.AbastecimentoResponse;
import br.edu.ifrn.frotaif.model.Abastecimento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = VeiculoMapper.class)
public interface AbastecimentoMapper {

    // O veículo vem da URL e é definido pelo service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "veiculo", ignore = true)
    Abastecimento toEntity(AbastecimentoRequest request);

    AbastecimentoResponse toResponse(Abastecimento abastecimento);

    List<AbastecimentoResponse> toResponseList(List<Abastecimento> abastecimentos);
}
