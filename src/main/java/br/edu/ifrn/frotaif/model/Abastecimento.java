package br.edu.ifrn.frotaif.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Abastecimento vinculado apenas ao veículo. */
@Entity
@Table(name = "abastecimentos")
@Getter
@Setter
@NoArgsConstructor
public class Abastecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Definido pelo service a partir da URL (/api/veiculos/{id}/abastecimentos)
    @ManyToOne(optional = false)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @NotNull
    @PastOrPresent
    @Column(nullable = false)
    private LocalDate data;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal litros;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long kmNoAbastecimento;

    @Column(length = 120)
    private String posto;
}
