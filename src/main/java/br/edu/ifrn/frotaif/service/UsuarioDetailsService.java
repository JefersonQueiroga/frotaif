package br.edu.ifrn.frotaif.service;

import br.edu.ifrn.frotaif.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// A ponte entre o Spring Security e o banco no login (POST /api/auth/login).
// Havendo este bean e um PasswordEncoder, o framework monta o AuthenticationManager sozinho.
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    public UsuarioDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    // "username" aqui é o e-mail informado no login
    @Override
    public UserDetails loadUserByUsername(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
