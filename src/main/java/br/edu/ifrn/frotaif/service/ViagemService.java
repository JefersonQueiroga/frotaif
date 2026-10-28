package br.edu.ifrn.frotaif.service;

import br.edu.ifrn.frotaif.config.ViagemProperties;
import br.edu.ifrn.frotaif.exception.RecursoNaoEncontradoException;
import br.edu.ifrn.frotaif.exception.RegraNegocioException;
import br.edu.ifrn.frotaif.model.Usuario;
import br.edu.ifrn.frotaif.model.Viagem;
import br.edu.ifrn.frotaif.model.enums.StatusViagem;
import br.edu.ifrn.frotaif.repository.UsuarioRepository;
import br.edu.ifrn.frotaif.repository.ViagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ViagemService {

    private final ViagemRepository viagemRepository;
    private final UsuarioRepository usuarioRepository;
    private final ViagemProperties viagemProperties;

    public ViagemService(ViagemRepository viagemRepository,
                         UsuarioRepository usuarioRepository,
                         ViagemProperties viagemProperties) {
        this.viagemRepository = viagemRepository;
        this.usuarioRepository = usuarioRepository;
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
}
