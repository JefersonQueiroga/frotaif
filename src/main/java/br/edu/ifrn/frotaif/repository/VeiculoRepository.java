package br.edu.ifrn.frotaif.repository;

import br.edu.ifrn.frotaif.model.Veiculo;
import br.edu.ifrn.frotaif.model.enums.StatusVeiculo;
import br.edu.ifrn.frotaif.model.enums.TipoVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    // RN01 — placa única (no cadastro e na edição)
    boolean existsByPlaca(String placa);

    boolean existsByPlacaAndIdNot(String placa, Long id);

    // RF01 — filtros por status e tipo
    List<Veiculo> findByStatus(StatusVeiculo status);

    List<Veiculo> findByStatusAndTipo(StatusVeiculo status, TipoVeiculo tipo);

    // RN02 — listagem padrão: todos, exceto os inativos
    List<Veiculo> findByStatusNot(StatusVeiculo status);

    List<Veiculo> findByStatusNotAndTipo(StatusVeiculo status, TipoVeiculo tipo);
}
