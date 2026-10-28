package br.edu.ifrn.frotaif.service;

import br.edu.ifrn.frotaif.exception.ConflitoException;
import br.edu.ifrn.frotaif.exception.RecursoNaoEncontradoException;
import br.edu.ifrn.frotaif.exception.RegraNegocioException;
import br.edu.ifrn.frotaif.model.Veiculo;
import br.edu.ifrn.frotaif.model.enums.StatusVeiculo;
import br.edu.ifrn.frotaif.model.enums.TipoVeiculo;
import br.edu.ifrn.frotaif.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public List<Veiculo> listar(StatusVeiculo status, TipoVeiculo tipo) {
        // RN02 — sem filtro de status, os inativos ficam de fora
        if (status == null) {
            return tipo == null
                    ? veiculoRepository.findByStatusNot(StatusVeiculo.INATIVO)
                    : veiculoRepository.findByStatusNotAndTipo(StatusVeiculo.INATIVO, tipo);
        }
        return tipo == null
                ? veiculoRepository.findByStatus(status)
                : veiculoRepository.findByStatusAndTipo(status, tipo);
    }

    public Veiculo buscarPorId(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo " + id + " não encontrado"));
    }

    @Transactional
    public Veiculo cadastrar(Veiculo veiculo) {
        // RN01 — placa única
        if (veiculoRepository.existsByPlaca(veiculo.getPlaca())) {
            throw new ConflitoException("Já existe veículo com a placa " + veiculo.getPlaca());
        }
        veiculo.setId(null);
        veiculo.setStatus(StatusVeiculo.DISPONIVEL);
        if (veiculo.getQuilometragemAtual() == null) {
            veiculo.setQuilometragemAtual(0L);
        }
        return veiculoRepository.save(veiculo);
    }

    @Transactional
    public Veiculo atualizar(Long id, Veiculo dados) {
        Veiculo veiculo = buscarPorId(id);

        // RN01 — placa única, desconsiderando o próprio veículo
        if (veiculoRepository.existsByPlacaAndIdNot(dados.getPlaca(), id)) {
            throw new ConflitoException("Já existe veículo com a placa " + dados.getPlaca());
        }

        // status e quilometragem não são editados aqui: mudam pelas regras de viagem/manutenção
        veiculo.setPlaca(dados.getPlaca());
        veiculo.setMarca(dados.getMarca());
        veiculo.setModelo(dados.getModelo());
        veiculo.setAno(dados.getAno());
        veiculo.setTipo(dados.getTipo());
        veiculo.setCapacidadePassageiros(dados.getCapacidadePassageiros());
        return veiculo; // entidade gerenciada: o Hibernate grava as mudanças ao fim da transação
    }

    @Transactional
    public void inativar(Long id) {
        // RN02 — exclusão lógica
        Veiculo veiculo = buscarPorId(id);
        veiculo.setStatus(StatusVeiculo.INATIVO);
    }

    @Transactional
    public Veiculo reativar(Long id) {
        Veiculo veiculo = buscarPorId(id);
        if (veiculo.getStatus() != StatusVeiculo.INATIVO) {
            throw new RegraNegocioException("Só é possível reativar um veículo inativo");
        }
        veiculo.setStatus(StatusVeiculo.DISPONIVEL);
        return veiculo;
    }
}
