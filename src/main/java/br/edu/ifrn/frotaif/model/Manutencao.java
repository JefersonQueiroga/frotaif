package br.edu.ifrn.frotaif.model;

import br.edu.ifrn.frotaif.model.enums.TipoManutencao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "manutencoes")
@Getter
@Setter
@NoArgsConstructor
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Definido pelo service a partir da URL (/api/veiculos/{id}/manutencoes)
    @ManyToOne(optional = false)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoManutencao tipo;

    @Column(length = 255)
    private String descricao;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataInicio;

    // Define o período usado para cancelar viagens aprovadas (RN12)
    @NotNull
    @Column(nullable = false)
    private LocalDate dataPrevistaFim;

    // Preenchidos no encerramento (RN13)
    private LocalDate dataFim;

    @Column(precision = 10, scale = 2)
    private BigDecimal custo;

    public boolean isAberta() {
        return dataFim == null;
    }
}
