package br.edu.ifrn.frotaif.mapper;

import br.edu.ifrn.frotaif.dto.manutencao.AbrirManutencaoRequest;
import br.edu.ifrn.frotaif.dto.manutencao.ManutencaoResponse;
import br.edu.ifrn.frotaif.model.Manutencao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = VeiculoMapper.class)
public interface ManutencaoMapper {

    // Veículo vem da URL; dataFim e custo só existem no encerramento (RN13)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "veiculo", ignore = true)
    @Mapping(target = "dataFim", ignore = true)
    @Mapping(target = "custo", ignore = true)
    Manutencao toEntity(AbrirManutencaoRequest request);

    ManutencaoResponse toResponse(Manutencao manutencao);

    List<ManutencaoResponse> toResponseList(List<Manutencao> manutencoes);
}
