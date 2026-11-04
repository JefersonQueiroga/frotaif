package br.edu.ifrn.frotaif.service;

import br.edu.ifrn.frotaif.dto.manutencao.AbrirManutencaoRequest;
import br.edu.ifrn.frotaif.dto.manutencao.EncerrarManutencaoRequest;
import br.edu.ifrn.frotaif.dto.manutencao.ManutencaoResponse;
import br.edu.ifrn.frotaif.exception.ConflitoException;
import br.edu.ifrn.frotaif.exception.RecursoNaoEncontradoException;
import br.edu.ifrn.frotaif.exception.RegraNegocioException;
import br.edu.ifrn.frotaif.mapper.ManutencaoMapper;
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
    private final ManutencaoMapper manutencaoMapper;

    public ManutencaoService(ManutencaoRepository manutencaoRepository,
                             VeiculoService veiculoService,
                             ViagemService viagemService,
                             ManutencaoMapper manutencaoMapper) {
        this.manutencaoRepository = manutencaoRepository;
        this.veiculoService = veiculoService;
        this.viagemService = viagemService;
        this.manutencaoMapper = manutencaoMapper;
    }

    public List<ManutencaoResponse> listar(Long veiculoId) {
        veiculoService.buscarEntidade(veiculoId); // 404 se o veículo não existir
        return manutencaoMapper.toResponseList(manutencaoRepository.findByVeiculoIdOrderByDataInicioDescIdDesc(veiculoId));
    }

    private Manutencao buscarEntidade(Long id) {
        return manutencaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Manutenção " + id + " não encontrada"));
    }

    @Transactional
    public ManutencaoResponse abrir(Long veiculoId, AbrirManutencaoRequest request) {
        Veiculo veiculo = veiculoService.buscarEntidade(veiculoId);

        // RN12 — bloqueada se o veículo estiver EM_VIAGEM. Também não abre se já estiver
        // em manutenção ou inativo (o encerramento o tornaria DISPONIVEL por engano).
        if (veiculo.getStatus() != StatusVeiculo.DISPONIVEL) {
            throw new ConflitoException("Não é possível abrir manutenção: o veículo " + veiculo.getPlaca()
                    + " está " + veiculo.getStatus());
        }
        if (request.dataPrevistaFim().isBefore(request.dataInicio())) {
            throw new RegraNegocioException("A data prevista de fim não pode ser anterior à data de início");
        }

        // O mapper não copia id, dataFim nem custo (só existem no encerramento)
        Manutencao manutencao = manutencaoMapper.toEntity(request);
        manutencao.setVeiculo(veiculo);
        manutencaoRepository.save(manutencao);

        // RN12 — veículo em manutenção e viagens APROVADAS do período canceladas,
        // tudo na mesma transação: ou acontece tudo, ou nada.
        veiculo.setStatus(StatusVeiculo.EM_MANUTENCAO);
        viagemService.cancelarAprovadasNoPeriodo(veiculo.getId(),
                manutencao.getDataInicio(), manutencao.getDataPrevistaFim(),
                "Cancelada automaticamente: manutenção " + manutencao.getId() + " do veículo " + veiculo.getPlaca());
        return manutencaoMapper.toResponse(manutencao);
    }

    @Transactional
    public ManutencaoResponse encerrar(Long id, EncerrarManutencaoRequest request) {
        Manutencao manutencao = buscarEntidade(id);
        if (!manutencao.isAberta()) {
            throw new RegraNegocioException("A manutenção " + id + " já foi encerrada em " + manutencao.getDataFim());
        }
        // RN13 — custo obrigatório (≥ 0) e data de fim válida
        if (request.custo() == null || request.custo().compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("Informe o custo da manutenção (maior ou igual a zero)");
        }
        if (request.dataFim() == null || request.dataFim().isBefore(manutencao.getDataInicio())) {
            throw new RegraNegocioException("Informe a data de fim (não anterior a " + manutencao.getDataInicio() + ")");
        }

        manutencao.setDataFim(request.dataFim());
        manutencao.setCusto(request.custo());
        manutencao.getVeiculo().setStatus(StatusVeiculo.DISPONIVEL); // RN13
        return manutencaoMapper.toResponse(manutencao);
    }
}
