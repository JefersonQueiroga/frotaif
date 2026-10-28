package br.edu.ifrn.frotaif.service;

import br.edu.ifrn.frotaif.config.ViagemProperties;
import br.edu.ifrn.frotaif.exception.ConflitoException;
import br.edu.ifrn.frotaif.exception.RecursoNaoEncontradoException;
import br.edu.ifrn.frotaif.exception.RegraNegocioException;
import br.edu.ifrn.frotaif.model.Motorista;
import br.edu.ifrn.frotaif.model.Usuario;
import br.edu.ifrn.frotaif.model.Veiculo;
import br.edu.ifrn.frotaif.model.Viagem;
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

    public ViagemService(ViagemRepository viagemRepository,
                         UsuarioRepository usuarioRepository,
                         VeiculoService veiculoService,
                         MotoristaService motoristaService,
                         ViagemProperties viagemProperties) {
        this.viagemRepository = viagemRepository;
        this.usuarioRepository = usuarioRepository;
        this.veiculoService = veiculoService;
        this.motoristaService = motoristaService;
        this.viagemProperties = viagemProperties;
    }

    public List<Viagem> listar(StatusViagem status, Long solicitanteId, LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            throw new RegraNegocioException("A data final do período não pode ser anterior à inicial");
        }
        // Período por dia inteiro: de inicio 00:00 até o fim do dia "fim"
        LocalDateTime inicioPeriodo = inicio == null ? null : inicio.atStartOfDay();
        LocalDateTime fimPeriodo = fim == null ? null : fim.plusDays(1).atStartOfDay();
        return viagemRepository.filtrar(status, solicitanteId, inicioPeriodo, fimPeriodo);
    }

    public Viagem buscarPorId(Long id) {
        return viagemRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Viagem " + id + " não encontrada"));
    }

    @Transactional
    public Viagem solicitar(Viagem viagem) {
        Usuario solicitante = usuarioRepository.findById(viagem.getSolicitante().getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário " + viagem.getSolicitante().getId() + " não encontrado"));
        if (!solicitante.isAtivo()) {
            throw new RegraNegocioException("Usuário inativo não pode solicitar viagens");
        }

        if (!viagem.getRetornoPrevisto().isAfter(viagem.getSaida())) {
            throw new RegraNegocioException("O retorno previsto deve ser posterior à saída");
        }

        // RN04 — antecedência mínima configurável
        int horas = viagemProperties.antecedenciaMinimaHoras();
        if (viagem.getSaida().isBefore(LocalDateTime.now().plusHours(horas))) {
            throw new RegraNegocioException("A viagem deve ser solicitada com pelo menos " + horas + "h de antecedência");
        }

        // Campos que o solicitante não define: veículo e motorista vêm na aprovação (RF06)
        viagem.setId(null);
        viagem.setSolicitante(solicitante);
        viagem.setVeiculo(null);
        viagem.setMotorista(null);
        viagem.setKmInicial(null);
        viagem.setKmFinal(null);
        viagem.setMotivo(null);
        viagem.setStatus(StatusViagem.SOLICITADA);
        return viagemRepository.save(viagem);
    }

    @Transactional
    public Viagem aprovar(Long id, Viagem dados) {
        Viagem viagem = buscarPorId(id);
        if (viagem.getStatus() != StatusViagem.SOLICITADA) {
            throw new RegraNegocioException("Só é possível aprovar uma viagem SOLICITADA (status atual: "
                    + viagem.getStatus() + ")");
        }
        if (dados.getVeiculo() == null || dados.getVeiculo().getId() == null
                || dados.getMotorista() == null || dados.getMotorista().getId() == null) {
            throw new RegraNegocioException("Informe o veículo e o motorista da viagem");
        }

        // buscarPorId dos outros services já lança 404 se não existir
        Veiculo veiculo = veiculoService.buscarPorId(dados.getVeiculo().getId());
        Motorista motorista = motoristaService.buscarPorId(dados.getMotorista().getId());

        validarVeiculo(viagem, veiculo);
        validarMotorista(viagem, motorista);
        validarCnh(viagem, motorista, veiculo);

        viagem.setVeiculo(veiculo);
        viagem.setMotorista(motorista);
        viagem.setStatus(StatusViagem.APROVADA);
        return viagem;
    }

    @Transactional
    public Viagem rejeitar(Long id, Viagem dados) {
        Viagem viagem = buscarPorId(id);
        if (viagem.getStatus() != StatusViagem.SOLICITADA) {
            throw new RegraNegocioException("Só é possível rejeitar uma viagem SOLICITADA (status atual: "
                    + viagem.getStatus() + ")");
        }
        if (dados.getMotivo() == null || dados.getMotivo().isBlank()) {
            throw new RegraNegocioException("Informe o motivo da rejeição");
        }
        viagem.setMotivo(dados.getMotivo());
        viagem.setStatus(StatusViagem.REJEITADA);
        return viagem;
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

    // RN08 — também será verificada ao iniciar a viagem (f5)
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
