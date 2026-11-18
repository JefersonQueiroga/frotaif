package br.edu.ifrn.frotaif.repository;

import br.edu.ifrn.frotaif.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Login (t3-security): o e-mail é o "username" do Spring Security
    Optional<Usuario> findByEmail(String email);
}
