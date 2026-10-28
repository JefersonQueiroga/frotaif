package br.edu.ifrn.frotaif.repository;

import br.edu.ifrn.frotaif.model.Viagem;
import br.edu.ifrn.frotaif.model.enums.StatusViagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ViagemRepository extends JpaRepository<Viagem, Long> {

    // RF09 — filtros opcionais: parâmetro nulo = filtro ignorado.
    // O cast nas datas é necessário no PostgreSQL: o driver envia data/hora sem tipo,
    // e em ":inicio is null" o banco não consegue descobrir qual é ("could not determine data type").
    @Query("""
            select v from Viagem v
            where (:status is null or v.status = :status)
              and (:solicitanteId is null or v.solicitante.id = :solicitanteId)
              and (cast(:inicio as LocalDateTime) is null or v.saida >= :inicio)
              and (cast(:fim as LocalDateTime) is null or v.saida < :fim)
            order by v.saida
            """)
    List<Viagem> filtrar(StatusViagem status, Long solicitanteId, LocalDateTime inicio, LocalDateTime fim);

    // RN06 — o veículo já tem viagem (nos status informados) que se sobrepõe ao período?
    // Dois períodos se sobrepõem quando um começa antes do outro terminar, e vice-versa.
    @Query("""
            select count(v) > 0 from Viagem v
            where v.veiculo.id = :veiculoId
              and v.status in :status
              and v.saida < :retorno
              and v.retornoPrevisto > :saida
            """)
    boolean existeConflitoDeVeiculo(Long veiculoId, Collection<StatusViagem> status,
                                    LocalDateTime saida, LocalDateTime retorno);

    // RN07 — mesma ideia para o motorista
    @Query("""
            select count(v) > 0 from Viagem v
            where v.motorista.id = :motoristaId
              and v.status in :status
              and v.saida < :retorno
              and v.retornoPrevisto > :saida
            """)
    boolean existeConflitoDeMotorista(Long motoristaId, Collection<StatusViagem> status,
                                      LocalDateTime saida, LocalDateTime retorno);

    // RN12 — viagens do veículo, num status, que se sobrepõem ao período (query method derivado do nome)
    List<Viagem> findByVeiculoIdAndStatusAndSaidaBeforeAndRetornoPrevistoAfter(
            Long veiculoId, StatusViagem status, LocalDateTime fimPeriodo, LocalDateTime inicioPeriodo);
}
