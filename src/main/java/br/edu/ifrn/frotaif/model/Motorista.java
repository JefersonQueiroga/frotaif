package br.edu.ifrn.frotaif.model;

import br.edu.ifrn.frotaif.model.enums.CategoriaCnh;
import br.edu.ifrn.frotaif.validation.CPF;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    // Somente dígitos; único (RN03)
    @NotBlank
    @CPF
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    // Único (RN03)
    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String registroCnh;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 2)
    private CategoriaCnh categoriaCnh;

    @NotNull
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
