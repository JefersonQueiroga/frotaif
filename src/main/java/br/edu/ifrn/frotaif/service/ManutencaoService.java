package br.edu.ifrn.frotaif.service;

import br.edu.ifrn.frotaif.exception.ConflitoException;
import br.edu.ifrn.frotaif.exception.RecursoNaoEncontradoException;
import br.edu.ifrn.frotaif.exception.RegraNegocioException;
import br.edu.ifrn.frotaif.model.Manutencao;
import br.edu.ifrn.frotaif.model.Veiculo;
import br.edu.ifrn.frotaif.model.enums.StatusVeiculo;
import br.edu.ifrn.frotaif.repository.ManutencaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ManutencaoService {

    private final ManutencaoRepository manutencaoRepository;
    private final VeiculoService veiculoService;
    private final ViagemService viagemService;

    public ManutencaoService(ManutencaoRepository manutencaoRepository,
                             VeiculoService veiculoService,
                             ViagemService viagemService) {
        this.manutencaoRepository = manutencaoRepository;
        this.veiculoService = veiculoService;
        this.viagemService = viagemService;
    }

    public List<Manutencao> listar(Long veiculoId) {
        veiculoService.buscarPorId(veiculoId); // 404 se o veículo não existir
        return manutencaoRepository.findByVeiculoIdOrderByDataInicioDescIdDesc(veiculoId);
    }

    public Manutencao buscarPorId(Long id) {
        return manutencaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Manutenção " + id + " não encontrada"));
    }

    @Transactional
    public Manutencao abrir(Long veiculoId, Manutencao manutencao) {
        Veiculo veiculo = veiculoService.buscarPorId(veiculoId);

        // RN12 — bloqueada se o veículo estiver EM_VIAGEM. Também não abre se já estiver
        // em manutenção ou inativo (o encerramento o tornaria DISPONIVEL por engano).
        if (veiculo.getStatus() != StatusVeiculo.DISPONIVEL) {
            throw new ConflitoException("Não é possível abrir manutenção: o veículo " + veiculo.getPlaca()
                    + " está " + veiculo.getStatus());
        }
        if (manutencao.getDataPrevistaFim().isBefore(manutencao.getDataInicio())) {
            throw new RegraNegocioException("A data prevista de fim não pode ser anterior à data de início");
        }

        manutencao.setId(null);
        manutencao.setVeiculo(veiculo);
        manutencao.setDataFim(null);
        manutencao.setCusto(null);
        manutencaoRepository.save(manutencao);

        // RN12 — veículo em manutenção e viagens APROVADAS do período canceladas,
        // tudo na mesma transação: ou acontece tudo, ou nada.
        veiculo.setStatus(StatusVeiculo.EM_MANUTENCAO);
        viagemService.cancelarAprovadasNoPeriodo(veiculo.getId(),
                manutencao.getDataInicio(), manutencao.getDataPrevistaFim(),
                "Cancelada automaticamente: manutenção " + manutencao.getId() + " do veículo " + veiculo.getPlaca());
        return manutencao;
    }

    @Transactional
    public Manutencao encerrar(Long id, Manutencao dados) {
        Manutencao manutencao = buscarPorId(id);
        if (!manutencao.isAberta()) {
            throw new RegraNegocioException("A manutenção " + id + " já foi encerrada em " + manutencao.getDataFim());
        }
        // RN13 — custo obrigatório (≥ 0) e data de fim válida
        if (dados.getCusto() == null || dados.getCusto().compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("Informe o custo da manutenção (maior ou igual a zero)");
        }
        if (dados.getDataFim() == null || dados.getDataFim().isBefore(manutencao.getDataInicio())) {
            throw new RegraNegocioException("Informe a data de fim (não anterior a " + manutencao.getDataInicio() + ")");
        }

        manutencao.setDataFim(dados.getDataFim());
        manutencao.setCusto(dados.getCusto());
        manutencao.getVeiculo().setStatus(StatusVeiculo.DISPONIVEL); // RN13
        return manutencao;
    }
}
