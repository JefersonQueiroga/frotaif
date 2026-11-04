package br.edu.ifrn.frotaif.service;

import br.edu.ifrn.frotaif.dto.abastecimento.AbastecimentoRequest;
import br.edu.ifrn.frotaif.dto.abastecimento.AbastecimentoResponse;
import br.edu.ifrn.frotaif.exception.RegraNegocioException;
import br.edu.ifrn.frotaif.mapper.AbastecimentoMapper;
import br.edu.ifrn.frotaif.model.Abastecimento;
import br.edu.ifrn.frotaif.model.Veiculo;
import br.edu.ifrn.frotaif.repository.AbastecimentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AbastecimentoService {

    private final AbastecimentoRepository abastecimentoRepository;
    private final VeiculoService veiculoService;
    private final AbastecimentoMapper abastecimentoMapper;

    public AbastecimentoService(AbastecimentoRepository abastecimentoRepository,
                                VeiculoService veiculoService,
                                AbastecimentoMapper abastecimentoMapper) {
        this.abastecimentoRepository = abastecimentoRepository;
        this.veiculoService = veiculoService;
        this.abastecimentoMapper = abastecimentoMapper;
    }

    public List<AbastecimentoResponse> listar(Long veiculoId) {
        veiculoService.buscarEntidade(veiculoId); // 404 se o veículo não existir
        return abastecimentoMapper.toResponseList(abastecimentoRepository.findByVeiculoIdOrderByDataDescIdDesc(veiculoId));
    }

    @Transactional
    public AbastecimentoResponse registrar(Long veiculoId, AbastecimentoRequest request) {
        Veiculo veiculo = veiculoService.buscarEntidade(veiculoId);

        // RN11 — o km não pode ser menor que o último conhecido. Como todo abastecimento
        // e toda viagem concluída atualizam a quilometragemAtual, ela é o último km conhecido.
        Long km = request.kmNoAbastecimento();
        if (km < veiculo.getQuilometragemAtual()) {
            throw new RegraNegocioException("O km informado (" + km + ") é menor que o último km conhecido do veículo ("
                    + veiculo.getQuilometragemAtual() + ")");
        }
        // RN11 — se for maior, atualiza a quilometragem do veículo
        if (km > veiculo.getQuilometragemAtual()) {
            veiculo.setQuilometragemAtual(km);
        }

        Abastecimento abastecimento = abastecimentoMapper.toEntity(request);
        abastecimento.setVeiculo(veiculo);
        return abastecimentoMapper.toResponse(abastecimentoRepository.save(abastecimento));
    }
}
