package br.edu.ifrn.frotaif.service;

import br.edu.ifrn.frotaif.dto.veiculo.VeiculoRequest;
import br.edu.ifrn.frotaif.dto.veiculo.VeiculoResponse;
import br.edu.ifrn.frotaif.exception.ConflitoException;
import br.edu.ifrn.frotaif.exception.RecursoNaoEncontradoException;
import br.edu.ifrn.frotaif.exception.RegraNegocioException;
import br.edu.ifrn.frotaif.mapper.VeiculoMapper;
import br.edu.ifrn.frotaif.model.Veiculo;
import br.edu.ifrn.frotaif.model.enums.StatusVeiculo;
import br.edu.ifrn.frotaif.model.enums.TipoVeiculo;
import br.edu.ifrn.frotaif.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Para o controller, o service fala DTO (Request entra, Response sai).
// Para os outros services, buscarEntidade devolve a entidade — as regras trabalham com o domínio.
@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final VeiculoMapper veiculoMapper;

    public VeiculoService(VeiculoRepository veiculoRepository, VeiculoMapper veiculoMapper) {
        this.veiculoRepository = veiculoRepository;
        this.veiculoMapper = veiculoMapper;
    }

    public List<VeiculoResponse> listar(StatusVeiculo status, TipoVeiculo tipo) {
        // RN02 — sem filtro de status, os inativos ficam de fora
        List<Veiculo> veiculos;
        if (status == null) {
            veiculos = tipo == null
                    ? veiculoRepository.findByStatusNot(StatusVeiculo.INATIVO)
                    : veiculoRepository.findByStatusNotAndTipo(StatusVeiculo.INATIVO, tipo);
        } else {
            veiculos = tipo == null
                    ? veiculoRepository.findByStatus(status)
                    : veiculoRepository.findByStatusAndTipo(status, tipo);
        }
        return veiculoMapper.toResponseList(veiculos);
    }

    public VeiculoResponse buscarPorId(Long id) {
        return veiculoMapper.toResponse(buscarEntidade(id));
    }

    // Usado pelos outros services (Viagem, Abastecimento, Manutenção)
    public Veiculo buscarEntidade(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo " + id + " não encontrado"));
    }

    @Transactional
    public VeiculoResponse cadastrar(VeiculoRequest request) {
        // RN01 — placa única
        if (veiculoRepository.existsByPlaca(request.placa())) {
            throw new ConflitoException("Já existe veículo com a placa " + request.placa());
        }
        // O mapper não copia id nem status: o veículo nasce DISPONIVEL (valor padrão da entidade)
        Veiculo veiculo = veiculoMapper.toEntity(request);
        if (veiculo.getQuilometragemAtual() == null) {
            veiculo.setQuilometragemAtual(0L);
        }
        return veiculoMapper.toResponse(veiculoRepository.save(veiculo));
    }

    @Transactional
    public VeiculoResponse atualizar(Long id, VeiculoRequest request) {
        Veiculo veiculo = buscarEntidade(id);

        // RN01 — placa única, desconsiderando o próprio veículo
        if (veiculoRepository.existsByPlacaAndIdNot(request.placa(), id)) {
            throw new ConflitoException("Já existe veículo com a placa " + request.placa());
        }

        // status e quilometragem não são editados aqui (ignorados no mapper)
        veiculoMapper.atualizar(request, veiculo);
        return veiculoMapper.toResponse(veiculo); // entidade gerenciada: o Hibernate grava ao fim da transação
    }

    @Transactional
    public void inativar(Long id) {
        // RN02 — exclusão lógica
        Veiculo veiculo = buscarEntidade(id);
        veiculo.setStatus(StatusVeiculo.INATIVO);
    }

    @Transactional
    public VeiculoResponse reativar(Long id) {
        Veiculo veiculo = buscarEntidade(id);
        if (veiculo.getStatus() != StatusVeiculo.INATIVO) {
            throw new RegraNegocioException("Só é possível reativar um veículo inativo");
        }
        veiculo.setStatus(StatusVeiculo.DISPONIVEL);
        return veiculoMapper.toResponse(veiculo);
    }
}
