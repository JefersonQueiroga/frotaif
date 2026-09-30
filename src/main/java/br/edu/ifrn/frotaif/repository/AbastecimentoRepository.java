package br.edu.ifrn.frotaif.repository;

import br.edu.ifrn.frotaif.model.Abastecimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbastecimentoRepository extends JpaRepository<Abastecimento, Long> {
    // Consultas específicas são adicionadas junto com cada funcionalidade.
}
