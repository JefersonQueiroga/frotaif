package br.edu.ifrn.frotaif.mapper;

import br.edu.ifrn.frotaif.dto.veiculo.VeiculoRequest;
import br.edu.ifrn.frotaif.dto.veiculo.VeiculoResponse;
import br.edu.ifrn.frotaif.dto.veiculo.VeiculoResumoResponse;
import br.edu.ifrn.frotaif.model.Veiculo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

// O MapStruct gera a implementação (VeiculoMapperImpl) na compilação: veja em target/generated-sources.
// Vira um bean Spring por causa do -Amapstruct.defaultComponentModel=spring no pom.xml.
@Mapper
public interface VeiculoMapper {

    // id e status são do sistema: o veículo sempre nasce DISPONIVEL
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Veiculo toEntity(VeiculoRequest request);

    VeiculoResponse toResponse(Veiculo veiculo);

    List<VeiculoResponse> toResponseList(List<Veiculo> veiculos);

    VeiculoResumoResponse toResumo(Veiculo veiculo);

    // Edição: copia o request para a entidade JÁ carregada (gerenciada pelo JPA).
    // Status e quilometragem não mudam aqui: mudam pelas viagens/abastecimentos/manutenções.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "quilometragemAtual", ignore = true)
    void atualizar(VeiculoRequest request, @MappingTarget Veiculo veiculo);
}
