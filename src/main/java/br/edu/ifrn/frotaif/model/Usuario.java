package br.edu.ifrn.frotaif.model;

import br.edu.ifrn.frotaif.model.enums.Papel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    // Nunca sai na API: as respostas usam DTOs (UsuarioResumoResponse), sem a senha
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Papel papel;

    // Exclusão lógica (RN02)
    @Column(nullable = false)
    private boolean ativo = true;
}
