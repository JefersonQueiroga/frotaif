package br.edu.ifrn.frotaif.service;

import br.edu.ifrn.frotaif.dto.motorista.MotoristaRequest;
import br.edu.ifrn.frotaif.dto.motorista.MotoristaResponse;
import br.edu.ifrn.frotaif.exception.ConflitoException;
import br.edu.ifrn.frotaif.exception.RecursoNaoEncontradoException;
import br.edu.ifrn.frotaif.exception.RegraNegocioException;
import br.edu.ifrn.frotaif.mapper.MotoristaMapper;
import br.edu.ifrn.frotaif.model.Motorista;
import br.edu.ifrn.frotaif.repository.MotoristaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MotoristaService {

    private final MotoristaRepository motoristaRepository;
    private final MotoristaMapper motoristaMapper;

    public MotoristaService(MotoristaRepository motoristaRepository, MotoristaMapper motoristaMapper) {
        this.motoristaRepository = motoristaRepository;
        this.motoristaMapper = motoristaMapper;
    }

    public List<MotoristaResponse> listar() {
        // RN02 — só os ativos
        return motoristaMapper.toResponseList(motoristaRepository.findByAtivoTrue());
    }

    public MotoristaResponse buscarPorId(Long id) {
        return motoristaMapper.toResponse(buscarEntidade(id));
    }

    // Usado pelo ViagemService na aprovação
    public Motorista buscarEntidade(Long id) {
        return motoristaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Motorista " + id + " não encontrado"));
    }

    public List<MotoristaResponse> listarComCnhVencendo(int dias) {
        if (dias < 0) {
            throw new RegraNegocioException("A quantidade de dias não pode ser negativa");
        }
        // RF04 — de hoje até hoje + N dias
        LocalDate hoje = LocalDate.now();
        return motoristaMapper.toResponseList(
                motoristaRepository.findByAtivoTrueAndValidadeCnhBetweenOrderByValidadeCnh(hoje, hoje.plusDays(dias)));
    }

    @Transactional
    public MotoristaResponse cadastrar(MotoristaRequest request) {
        // RN03 — CPF e CNH únicos
        if (motoristaRepository.existsByCpf(request.cpf())) {
            throw new ConflitoException("Já existe motorista com o CPF " + request.cpf());
        }
        if (motoristaRepository.existsByRegistroCnh(request.registroCnh())) {
            throw new ConflitoException("Já existe motorista com a CNH " + request.registroCnh());
        }
        // O mapper não copia id, ativo nem usuario (o vínculo com o login entra em t3-security)
        Motorista motorista = motoristaMapper.toEntity(request);
        return motoristaMapper.toResponse(motoristaRepository.save(motorista));
    }

    @Transactional
    public MotoristaResponse atualizar(Long id, MotoristaRequest request) {
        Motorista motorista = buscarEntidade(id);

        // RN03 — CPF e CNH únicos, desconsiderando o próprio motorista
        if (motoristaRepository.existsByCpfAndIdNot(request.cpf(), id)) {
            throw new ConflitoException("Já existe motorista com o CPF " + request.cpf());
        }
        if (motoristaRepository.existsByRegistroCnhAndIdNot(request.registroCnh(), id)) {
            throw new ConflitoException("Já existe motorista com a CNH " + request.registroCnh());
        }

        motoristaMapper.atualizar(request, motorista);
        return motoristaMapper.toResponse(motorista); // dirty checking: o UPDATE sai no commit da transação
    }

    @Transactional
    public void inativar(Long id) {
        // RN02 — exclusão lógica
        Motorista motorista = buscarEntidade(id);
        motorista.setAtivo(false);
    }
}
