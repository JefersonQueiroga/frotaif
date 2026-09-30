package br.edu.ifrn.frotaif.model;

import br.edu.ifrn.frotaif.model.enums.CategoriaCnh;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/** Motorista terceirizado — identificado por CPF e registro da CNH. */
@Entity
@Table(name = "motoristas")
@Getter
@Setter
@NoArgsConstructor
public class Motorista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    // Somente dígitos; único (RN03)
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    // Único (RN03)
    @Column(nullable = false, unique = true, length = 20)
    private String registroCnh;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 2)
    private CategoriaCnh categoriaCnh;

    @Column(nullable = false)
    private LocalDate validadeCnh;

    // Login do motorista (papel MOTORISTA). Opcional até a aula de Security.
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    // Exclusão lógica (RN02)
    @Column(nullable = false)
    private boolean ativo = true;
}
