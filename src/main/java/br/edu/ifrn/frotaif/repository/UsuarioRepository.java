package br.edu.ifrn.frotaif.repository;

import br.edu.ifrn.frotaif.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Consultas específicas são adicionadas junto com cada funcionalidade.
}
