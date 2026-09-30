package br.edu.ifrn.frotaif.repository;

import br.edu.ifrn.frotaif.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    // Consultas específicas são adicionadas junto com cada funcionalidade.
}
