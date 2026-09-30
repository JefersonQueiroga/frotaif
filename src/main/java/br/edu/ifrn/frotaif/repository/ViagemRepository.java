package br.edu.ifrn.frotaif.repository;

import br.edu.ifrn.frotaif.model.Viagem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViagemRepository extends JpaRepository<Viagem, Long> {
    // Consultas específicas são adicionadas junto com cada funcionalidade.
}
