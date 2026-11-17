package br.edu.ifrn.frotaif.mapper;

import br.edu.ifrn.frotaif.dto.viagem.SolicitarViagemRequest;
import br.edu.ifrn.frotaif.dto.viagem.ViagemResponse;
import br.edu.ifrn.frotaif.model.Viagem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

// "uses": para converter solicitante, veiculo e motorista, reaproveita os outros mappers (toResumo)
@Mapper(uses = {UsuarioMapper.class, VeiculoMapper.class, MotoristaMapper.class})
public interface ViagemMapper {

    // O solicitante é buscado pelo service a partir de solicitanteId.
    // Os demais campos são definidos depois, pelas regras de aprovação e execução (RF06, RN10).
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "solicitante", ignore = true)
    @Mapping(target = "veiculo", ignore = true)
    @Mapping(target = "motorista", ignore = true)
    @Mapping(target = "kmInicial", ignore = true)
    @Mapping(target = "kmFinal", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "motivo", ignore = true)
    Viagem toEntity(SolicitarViagemRequest request);

    ViagemResponse toResponse(Viagem viagem);

    List<ViagemResponse> toResponseList(List<Viagem> viagens);
}
