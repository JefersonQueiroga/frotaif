package br.edu.ifrn.frotaif.service;

import br.edu.ifrn.frotaif.exception.ConflitoException;
import br.edu.ifrn.frotaif.exception.RecursoNaoEncontradoException;
import br.edu.ifrn.frotaif.exception.RegraNegocioException;
import br.edu.ifrn.frotaif.model.Motorista;
import br.edu.ifrn.frotaif.repository.MotoristaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MotoristaService {

    private final MotoristaRepository motoristaRepository;

    public MotoristaService(MotoristaRepository motoristaRepository) {
        this.motoristaRepository = motoristaRepository;
    }

    public List<Motorista> listar() {
        // RN02 — só os ativos
        return motoristaRepository.findByAtivoTrue();
    }

    public Motorista buscarPorId(Long id) {
        return motoristaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Motorista " + id + " não encontrado"));
    }

    public List<Motorista> listarComCnhVencendo(int dias) {
        if (dias < 0) {
            throw new RegraNegocioException("A quantidade de dias não pode ser negativa");
        }
        // RF04 — de hoje até hoje + N dias
        LocalDate hoje = LocalDate.now();
        return motoristaRepository.findByAtivoTrueAndValidadeCnhBetweenOrderByValidadeCnh(hoje, hoje.plusDays(dias));
    }

    @Transactional
    public Motorista cadastrar(Motorista motorista) {
        // RN03 — CPF e CNH únicos
        if (motoristaRepository.existsByCpf(motorista.getCpf())) {
            throw new ConflitoException("Já existe motorista com o CPF " + motorista.getCpf());
        }
        if (motoristaRepository.existsByRegistroCnh(motorista.getRegistroCnh())) {
            throw new ConflitoException("Já existe motorista com a CNH " + motorista.getRegistroCnh());
        }
        motorista.setId(null);
        motorista.setAtivo(true);
        motorista.setUsuario(null); // o vínculo com o login entra na aula de Security (t3-security)
        return motoristaRepository.save(motorista);
    }

    @Transactional
    public Motorista atualizar(Long id, Motorista dados) {
        Motorista motorista = buscarPorId(id);

        // RN03 — CPF e CNH únicos, desconsiderando o próprio motorista
        if (motoristaRepository.existsByCpfAndIdNot(dados.getCpf(), id)) {
            throw new ConflitoException("Já existe motorista com o CPF " + dados.getCpf());
        }
        if (motoristaRepository.existsByRegistroCnhAndIdNot(dados.getRegistroCnh(), id)) {
            throw new ConflitoException("Já existe motorista com a CNH " + dados.getRegistroCnh());
        }

        motorista.setNome(dados.getNome());
        motorista.setCpf(dados.getCpf());
        motorista.setRegistroCnh(dados.getRegistroCnh());
        motorista.setCategoriaCnh(dados.getCategoriaCnh());
        motorista.setValidadeCnh(dados.getValidadeCnh());
        return motorista; // dirty checking: o UPDATE sai no commit da transação
    }

    @Transactional
    public void inativar(Long id) {
        // RN02 — exclusão lógica
        Motorista motorista = buscarPorId(id);
        motorista.setAtivo(false);
    }
}
