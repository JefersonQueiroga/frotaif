package br.edu.ifrn.frotaif.service;

import br.edu.ifrn.frotaif.config.ViagemProperties;
import br.edu.ifrn.frotaif.dto.viagem.AprovarViagemRequest;
import br.edu.ifrn.frotaif.dto.viagem.ConcluirViagemRequest;
import br.edu.ifrn.frotaif.dto.viagem.MotivoRequest;
import br.edu.ifrn.frotaif.dto.viagem.SolicitarViagemRequest;
import br.edu.ifrn.frotaif.dto.viagem.ViagemResponse;
import br.edu.ifrn.frotaif.exception.AcessoNegadoException;
import br.edu.ifrn.frotaif.exception.ConflitoException;
import br.edu.ifrn.frotaif.exception.RecursoNaoEncontradoException;
import br.edu.ifrn.frotaif.exception.RegraNegocioException;
import br.edu.ifrn.frotaif.mapper.ViagemMapper;
import br.edu.ifrn.frotaif.model.Motorista;
import br.edu.ifrn.frotaif.model.Usuario;
import br.edu.ifrn.frotaif.model.Veiculo;
import br.edu.ifrn.frotaif.model.Viagem;
import br.edu.ifrn.frotaif.model.enums.Papel;
import br.edu.ifrn.frotaif.model.enums.StatusVeiculo;
import br.edu.ifrn.frotaif.model.enums.StatusViagem;
import br.edu.ifrn.frotaif.model.enums.TipoVeiculo;
import br.edu.ifrn.frotaif.repository.UsuarioRepository;
import br.edu.ifrn.frotaif.repository.ViagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ViagemService {

    // Viagens nesses status "ocupam" o veículo e o motorista no período (RN06, RN07)
    private static final List<StatusViagem> STATUS_QUE_OCUPAM =
            List.of(StatusViagem.APROVADA, StatusViagem.EM_ANDAMENTO);

    private final ViagemRepository viagemRepository;
    private final UsuarioRepository usuarioRepository;
    private final VeiculoService veiculoService;
    private final MotoristaService motoristaService;
    private final ViagemProperties viagemProperties;
    private final ViagemMapper viagemMapper;

    public ViagemService(ViagemRepository viagemRepository,
                         UsuarioRepository usuarioRepository,
                         VeiculoService veiculoService,
                         MotoristaService motoristaService,
                         ViagemProperties viagemProperties,
                         ViagemMapper viagemMapper) {
        this.viagemRepository = viagemRepository;
        this.usuarioRepository = usuarioRepository;
        this.veiculoService = veiculoService;
        this.motoristaService = motoristaService;
        this.viagemProperties = viagemProperties;
        this.viagemMapper = viagemMapper;
    }

    public List<ViagemResponse> listar(StatusViagem status, Long solicitanteId, LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            throw new RegraNegocioException("A data final do período não pode ser anterior à inicial");
        }
        // Período por dia inteiro: de inicio 00:00 até o fim do dia "fim"
        LocalDateTime inicioPeriodo = inicio == null ? null : inicio.atStartOfDay();
        LocalDateTime fimPeriodo = fim == null ? null : fim.plusDays(1).atStartOfDay();
        return viagemMapper.toResponseList(viagemRepository.filtrar(status, solicitanteId, inicioPeriodo, fimPeriodo));
    }

    public ViagemResponse buscarPorId(Long id) {
        return viagemMapper.toResponse(buscarEntidade(id));
    }

    private Viagem buscarEntidade(Long id) {
        return viagemRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Viagem " + id + " não encontrada"));
    }

    @Transactional
    public ViagemResponse solicitar(SolicitarViagemRequest request) {
        Usuario solicitante = buscarUsuario(request.solicitanteId());
        if (!solicitante.isAtivo()) {
            throw new RegraNegocioException("Usuário inativo não pode solicitar viagens");
        }

        if (!request.retornoPrevisto().isAfter(request.saida())) {
            throw new RegraNegocioException("O retorno previsto deve ser posterior à saída");
        }

        // RN04 — antecedência mínima configurável
        int horas = viagemProperties.antecedenciaMinimaHoras();
        if (request.saida().isBefore(LocalDateTime.now().plusHours(horas))) {
            throw new RegraNegocioException("A viagem deve ser solicitada com pelo menos " + horas + "h de antecedência");
        }

        // O request nem tem veículo, motorista, km ou status: o cliente não consegue defini-los (RF06).
        // A viagem nasce SOLICITADA (valor padrão da entidade).
        Viagem viagem = viagemMapper.toEntity(request);
        viagem.setSolicitante(solicitante);
        return viagemMapper.toResponse(viagemRepository.save(viagem));
    }

    @Transactional
    public ViagemResponse aprovar(Long id, AprovarViagemRequest request) {
        Viagem viagem = buscarEntidade(id);
        validarTransicao(viagem, StatusViagem.APROVADA);
        if (request.veiculoId() == null || request.motoristaId() == null) {
            throw new RegraNegocioException("Informe o veículo e o motorista da viagem");
        }

        // buscarEntidade dos outros services já lança 404 se não existir
        Veiculo veiculo = veiculoService.buscarEntidade(request.veiculoId());
        Motorista motorista = motoristaService.buscarEntidade(request.motoristaId());

        validarVeiculo(viagem, veiculo);
        validarMotorista(viagem, motorista);
        validarCnh(viagem, motorista, veiculo);

        viagem.setVeiculo(veiculo);
        viagem.setMotorista(motorista);
        viagem.setStatus(StatusViagem.APROVADA);
        return viagemMapper.toResponse(viagem);
    }

    @Transactional
    public ViagemResponse rejeitar(Long id, MotivoRequest request) {
        Viagem viagem = buscarEntidade(id);
        validarTransicao(viagem, StatusViagem.REJEITADA);
        if (request.motivo() == null || request.motivo().isBlank()) {
            throw new RegraNegocioException("Informe o motivo da rejeição");
        }
        viagem.setMotivo(request.motivo());
        viagem.setStatus(StatusViagem.REJEITADA);
        return viagemMapper.toResponse(viagem);
    }

    // usuarioId: quem está agindo (header X-Usuario-Id até a aula de Security)
    @Transactional
    public ViagemResponse iniciar(Long id, Long usuarioId) {
        Viagem viagem = buscarEntidade(id);
        verificarMotoristaAlocado(viagem, usuarioId); // RN09
        validarTransicao(viagem, StatusViagem.EM_ANDAMENTO);

        Veiculo veiculo = viagem.getVeiculo();
        if (veiculo.getStatus() != StatusVeiculo.DISPONIVEL) {
            throw new ConflitoException("O veículo " + veiculo.getPlaca() + " não está disponível (status: "
                    + veiculo.getStatus() + ")");
        }
        validarCnh(viagem, viagem.getMotorista(), veiculo); // RN08 — verificada de novo ao iniciar

        // RN10 — km inicial vem do veículo; veículo sai para a viagem
        viagem.setKmInicial(veiculo.getQuilometragemAtual());
        veiculo.setStatus(StatusVeiculo.EM_VIAGEM);
        viagem.setStatus(StatusViagem.EM_ANDAMENTO);
        return viagemMapper.toResponse(viagem); // dirty checking grava viagem E veículo na mesma transação
    }

    @Transactional
    public ViagemResponse concluir(Long id, Long usuarioId, ConcluirViagemRequest request) {
        Viagem viagem = buscarEntidade(id);
        verificarMotoristaAlocado(viagem, usuarioId); // RN09
        validarTransicao(viagem, StatusViagem.CONCLUIDA);

        // RN10 — km final maior que o inicial; atualiza o veículo e o libera
        Long kmFinal = request.kmFinal();
        if (kmFinal == null || kmFinal <= viagem.getKmInicial()) {
            throw new RegraNegocioException("O km final deve ser maior que o km inicial (" + viagem.getKmInicial() + ")");
        }
        Veiculo veiculo = viagem.getVeiculo();
        veiculo.setQuilometragemAtual(kmFinal);
        veiculo.setStatus(StatusVeiculo.DISPONIVEL);

        viagem.setKmFinal(kmFinal);
        viagem.setStatus(StatusViagem.CONCLUIDA);
        return viagemMapper.toResponse(viagem);
    }

    @Transactional
    public ViagemResponse cancelar(Long id, Long usuarioId, MotivoRequest request) {
        Viagem viagem = buscarEntidade(id);
        Usuario usuario = buscarUsuario(usuarioId);

        // RN14 — o gestor cancela qualquer viagem; os demais, só as próprias
        boolean gestor = usuario.getPapel() == Papel.GESTOR;
        boolean solicitante = viagem.getSolicitante().getId().equals(usuario.getId());
        if (!gestor && !solicitante) {
            throw new AcessoNegadoException("Só o solicitante ou um gestor pode cancelar esta viagem");
        }
        // RN14 — apenas enquanto SOLICITADA ou APROVADA
        validarTransicao(viagem, StatusViagem.CANCELADA);

        if (request.motivo() == null || request.motivo().isBlank()) {
            throw new RegraNegocioException("Informe o motivo do cancelamento");
        }
        viagem.setMotivo(request.motivo());
        viagem.setStatus(StatusViagem.CANCELADA);
        return viagemMapper.toResponse(viagem);
    }

    // RN12 — chamado pelo ManutencaoService ao abrir uma manutenção (uso interno: trabalha com entidades).
    // Roda dentro da transação de quem chamou: se a manutenção falhar, os cancelamentos são desfeitos.
    @Transactional
    public List<Viagem> cancelarAprovadasNoPeriodo(Long veiculoId, LocalDate inicio, LocalDate fim, String motivo) {
        List<Viagem> viagens = viagemRepository.findByVeiculoIdAndStatusAndSaidaBeforeAndRetornoPrevistoAfter(
                veiculoId, StatusViagem.APROVADA, fim.plusDays(1).atStartOfDay(), inicio.atStartOfDay());
        for (Viagem viagem : viagens) {
            validarTransicao(viagem, StatusViagem.CANCELADA);
            viagem.setMotivo(motivo);
            viagem.setStatus(StatusViagem.CANCELADA);
        }
        return viagens;
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário " + id + " não encontrado"));
    }

    // RN09 — transições só conforme o ciclo de vida (StatusViagem.podeMudarPara)
    private void validarTransicao(Viagem viagem, StatusViagem novoStatus) {
        if (!viagem.getStatus().podeMudarPara(novoStatus)) {
            throw new RegraNegocioException("Não é possível mudar a viagem de " + viagem.getStatus()
                    + " para " + novoStatus);
        }
    }

    // RN09 — só o motorista alocado inicia/conclui
    private void verificarMotoristaAlocado(Viagem viagem, Long usuarioId) {
        Motorista motorista = viagem.getMotorista();
        boolean ehOMotorista = motorista != null
                && motorista.getUsuario() != null
                && motorista.getUsuario().getId().equals(usuarioId);
        if (!ehOMotorista) {
            throw new AcessoNegadoException("Só o motorista alocado pode iniciar ou concluir esta viagem");
        }
    }

    private void validarVeiculo(Viagem viagem, Veiculo veiculo) {
        // RN05 — capacidade
        if (viagem.getQtdPassageiros() > veiculo.getCapacidadePassageiros()) {
            throw new RegraNegocioException("O veículo " + veiculo.getPlaca() + " comporta "
                    + veiculo.getCapacidadePassageiros() + " passageiros, mas a viagem tem "
                    + viagem.getQtdPassageiros());
        }
        // RN06 — disponível e sem outra viagem no mesmo período
        if (veiculo.getStatus() != StatusVeiculo.DISPONIVEL) {
            throw new ConflitoException("O veículo " + veiculo.getPlaca() + " não está disponível (status: "
                    + veiculo.getStatus() + ")");
        }
        if (viagemRepository.existeConflitoDeVeiculo(veiculo.getId(), STATUS_QUE_OCUPAM,
                viagem.getSaida(), viagem.getRetornoPrevisto())) {
            throw new ConflitoException("O veículo " + veiculo.getPlaca() + " já tem viagem no período");
        }
    }

    private void validarMotorista(Viagem viagem, Motorista motorista) {
        // RN07 — ativo e sem outra viagem no mesmo período
        if (!motorista.isAtivo()) {
            throw new ConflitoException("O motorista " + motorista.getNome() + " está inativo");
        }
        if (viagemRepository.existeConflitoDeMotorista(motorista.getId(), STATUS_QUE_OCUPAM,
                viagem.getSaida(), viagem.getRetornoPrevisto())) {
            throw new ConflitoException("O motorista " + motorista.getNome() + " já tem viagem no período");
        }
    }

    // RN08 — usada na aprovação e de novo ao iniciar
    private void validarCnh(Viagem viagem, Motorista motorista, Veiculo veiculo) {
        // A CNH precisa valer até o fim da viagem
        if (motorista.getValidadeCnh().isBefore(viagem.getRetornoPrevisto().toLocalDate())) {
            throw new RegraNegocioException("A CNH de " + motorista.getNome() + " vence em "
                    + motorista.getValidadeCnh() + ", antes do fim da viagem");
        }
        boolean veiculoGrande = veiculo.getTipo() == TipoVeiculo.VAN || veiculo.getTipo() == TipoVeiculo.ONIBUS;
        if (veiculoGrande && !motorista.getCategoriaCnh().habilitaVeiculoGrande()) {
            throw new RegraNegocioException(veiculo.getTipo() + " exige CNH categoria D ou superior; "
                    + motorista.getNome() + " tem categoria " + motorista.getCategoriaCnh());
        }
    }
}
