package br.edu.ifrn.frotaif.repository;

import br.edu.ifrn.frotaif.model.Manutencao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManutencaoRepository extends JpaRepository<Manutencao, Long> {

    // Manutenções de um veículo, da mais recente para a mais antiga
    List<Manutencao> findByVeiculoIdOrderByDataInicioDescIdDesc(Long veiculoId);
}
