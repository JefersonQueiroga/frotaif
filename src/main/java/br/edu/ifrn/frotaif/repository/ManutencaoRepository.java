package br.edu.ifrn.frotaif.repository;

import br.edu.ifrn.frotaif.model.Manutencao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManutencaoRepository extends JpaRepository<Manutencao, Long> {
    // Consultas específicas são adicionadas junto com cada funcionalidade.
}
