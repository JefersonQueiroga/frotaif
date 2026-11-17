package br.edu.ifrn.frotaif.dto.viagem;

import br.edu.ifrn.frotaif.dto.motorista.MotoristaResumoResponse;
import br.edu.ifrn.frotaif.dto.usuario.UsuarioResumoResponse;
import br.edu.ifrn.frotaif.dto.veiculo.VeiculoResumoResponse;
import br.edu.ifrn.frotaif.model.enums.StatusViagem;

import java.time.LocalDateTime;

public record ViagemResponse(
        Long id,
        UsuarioResumoResponse solicitante,
        String destino,
        String finalidade,
        LocalDateTime saida,
        LocalDateTime retornoPrevisto,
        Integer qtdPassageiros,
        VeiculoResumoResponse veiculo,      // null até a aprovação
        MotoristaResumoResponse motorista,  // null até a aprovação
        Long kmInicial,
        Long kmFinal,
        StatusViagem status,
        String motivo
) {
}
