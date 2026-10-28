package br.edu.ifrn.frotaif.repository;

import br.edu.ifrn.frotaif.model.Abastecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbastecimentoRepository extends JpaRepository<Abastecimento, Long> {

    // RF10 — abastecimentos de um veículo, do mais recente para o mais antigo
    List<Abastecimento> findByVeiculoIdOrderByDataDescIdDesc(Long veiculoId);
}
