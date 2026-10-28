package br.edu.ifrn.frotaif.service;

import br.edu.ifrn.frotaif.exception.RegraNegocioException;
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

    public AbastecimentoService(AbastecimentoRepository abastecimentoRepository, VeiculoService veiculoService) {
        this.abastecimentoRepository = abastecimentoRepository;
        this.veiculoService = veiculoService;
    }

    public List<Abastecimento> listar(Long veiculoId) {
        veiculoService.buscarPorId(veiculoId); // 404 se o veículo não existir
        return abastecimentoRepository.findByVeiculoIdOrderByDataDescIdDesc(veiculoId);
    }

    @Transactional
    public Abastecimento registrar(Long veiculoId, Abastecimento abastecimento) {
        Veiculo veiculo = veiculoService.buscarPorId(veiculoId);

        // RN11 — o km não pode ser menor que o último conhecido. Como todo abastecimento
        // e toda viagem concluída atualizam a quilometragemAtual, ela é o último km conhecido.
        Long km = abastecimento.getKmNoAbastecimento();
        if (km < veiculo.getQuilometragemAtual()) {
            throw new RegraNegocioException("O km informado (" + km + ") é menor que o último km conhecido do veículo ("
                    + veiculo.getQuilometragemAtual() + ")");
        }
        // RN11 — se for maior, atualiza a quilometragem do veículo
        if (km > veiculo.getQuilometragemAtual()) {
            veiculo.setQuilometragemAtual(km);
        }

        abastecimento.setId(null);
        abastecimento.setVeiculo(veiculo);
        return abastecimentoRepository.save(abastecimento);
    }
}
