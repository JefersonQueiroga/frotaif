package br.edu.ifrn.frotaif.repository;

import br.edu.ifrn.frotaif.model.Motorista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MotoristaRepository extends JpaRepository<Motorista, Long> {

    // RN03 — CPF e registro da CNH únicos (no cadastro e na edição)
    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByRegistroCnh(String registroCnh);

    boolean existsByRegistroCnhAndIdNot(String registroCnh, Long id);

    // RN02 — listagem só com os ativos
    List<Motorista> findByAtivoTrue();

    // RF04 — CNH vencendo entre duas datas
    List<Motorista> findByAtivoTrueAndValidadeCnhBetweenOrderByValidadeCnh(LocalDate inicio, LocalDate fim);
}
