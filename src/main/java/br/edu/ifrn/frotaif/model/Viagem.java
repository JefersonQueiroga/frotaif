package br.edu.ifrn.frotaif.model;

import br.edu.ifrn.frotaif.model.enums.StatusViagem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "viagens")
@Getter
@Setter
@NoArgsConstructor
public class Viagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @Column(nullable = false, length = 150)
    private String destino;

    @Column(nullable = false, length = 255)
    private String finalidade;

    @Column(nullable = false)
    private LocalDateTime saida;

    @Column(nullable = false)
    private LocalDateTime retornoPrevisto;

    @Column(nullable = false)
    private Integer qtdPassageiros;

    // Definidos pelo gestor na aprovação (RF06)
    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    @ManyToOne
    @JoinColumn(name = "motorista_id")
    private Motorista motorista;

    // Preenchidos ao iniciar / concluir (RN10)
    private Long kmInicial;
    private Long kmFinal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusViagem status = StatusViagem.SOLICITADA;

    // Motivo de rejeição ou cancelamento
    @Column(length = 255)
    private String motivo;
}
