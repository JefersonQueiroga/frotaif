package br.edu.ifrn.frotaif.model;

import br.edu.ifrn.frotaif.model.enums.StatusVeiculo;
import br.edu.ifrn.frotaif.model.enums.TipoVeiculo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "veiculos")
@Getter
@Setter
@NoArgsConstructor
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Formato Mercosul AAA0A00 (RN01)
    @NotBlank
    @Pattern(regexp = "[A-Z]{3}[0-9][A-Z][0-9]{2}", message = "placa deve estar no formato Mercosul AAA0A00")
    @Column(nullable = false, unique = true, length = 7)
    private String placa;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String marca;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String modelo;

    private Integer ano;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoVeiculo tipo;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer capacidadePassageiros;

    @Column(nullable = false)
    private Long quilometragemAtual = 0L;

    // INATIVO = exclusão lógica (RN02)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusVeiculo status = StatusVeiculo.DISPONIVEL;
}
