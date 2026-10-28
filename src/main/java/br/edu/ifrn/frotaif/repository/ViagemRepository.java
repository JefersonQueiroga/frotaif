package br.edu.ifrn.frotaif.repository;

import br.edu.ifrn.frotaif.model.Viagem;
import br.edu.ifrn.frotaif.model.enums.StatusViagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ViagemRepository extends JpaRepository<Viagem, Long> {

    // RF09 — filtros opcionais: parâmetro nulo = filtro ignorado
    @Query("""
            select v from Viagem v
            where (:status is null or v.status = :status)
              and (:solicitanteId is null or v.solicitante.id = :solicitanteId)
              and (:inicio is null or v.saida >= :inicio)
              and (:fim is null or v.saida < :fim)
            order by v.saida
            """)
    List<Viagem> filtrar(StatusViagem status, Long solicitanteId, LocalDateTime inicio, LocalDateTime fim);
}
