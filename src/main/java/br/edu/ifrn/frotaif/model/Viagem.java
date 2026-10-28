package br.edu.ifrn.frotaif.model;

import br.edu.ifrn.frotaif.model.enums.StatusViagem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    // Até a aula de Security, o cliente informa o id do solicitante: {"solicitante": {"id": 2}}
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String destino;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String finalidade;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime saida;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime retornoPrevisto;

    @NotNull
    @Positive
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
