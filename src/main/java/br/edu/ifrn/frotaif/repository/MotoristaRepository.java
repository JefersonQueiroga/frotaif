package br.edu.ifrn.frotaif.repository;

import br.edu.ifrn.frotaif.model.Motorista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotoristaRepository extends JpaRepository<Motorista, Long> {
    // Consultas específicas são adicionadas junto com cada funcionalidade.
}
