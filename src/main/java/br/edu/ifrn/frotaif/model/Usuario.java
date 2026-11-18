package br.edu.ifrn.frotaif.model;

import br.edu.ifrn.frotaif.model.enums.Papel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// UserDetails: o contrato que o Spring Security entende como "usuário"
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    // Hash BCrypt, nunca a senha. Nunca sai na API: as respostas usam DTOs (UsuarioResumoResponse)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Papel papel;

    // Exclusão lógica (RN02)
    @Column(nullable = false)
    private boolean ativo = true;

    // O papel GESTOR vira a authority ROLE_GESTOR (é o que hasRole("GESTOR") procura)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + papel.name()));
    }

    // O login do FrotaIF é o e-mail
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    // Usuário inativado (RN02) não entra mais: o login responde 401
    @Override
    public boolean isEnabled() {
        return ativo;
    }
}
