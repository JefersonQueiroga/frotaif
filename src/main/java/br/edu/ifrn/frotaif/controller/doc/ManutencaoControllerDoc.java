package br.edu.ifrn.frotaif.controller.doc;

import br.edu.ifrn.frotaif.model.Manutencao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

// Documentação da API (Swagger) do ManutencaoController — o controller só implementa esta interface
@Tag(name = "Manutenções", description = "Abertura e encerramento de manutenções (RF11, RF12)")
public interface ManutencaoControllerDoc {

    @Operation(summary = "Lista as manutenções de um veículo", description = "Da mais recente para a mais antiga.")
    @ApiResponse(responseCode = "200", description = "Lista de manutenções")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    List<Manutencao> listar(@Parameter(description = "Id do veículo", example = "3") Long veiculoId);

    @Operation(summary = "Abre uma manutenção",
            description = "O veículo precisa estar DISPONIVEL. Ele passa a EM_MANUTENCAO e as viagens APROVADAS "
                    + "do veículo no período [dataInicio, dataPrevistaFim] são canceladas automaticamente (RN12).")
    @ApiResponse(responseCode = "201", description = "Manutenção aberta")
    @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    @ApiResponse(responseCode = "409", description = "Veículo em viagem, já em manutenção ou inativo (RN12)")
    @ApiResponse(responseCode = "422", description = "Data prevista de fim anterior ao início")
    Manutencao abrir(@Parameter(description = "Id do veículo", example = "1") Long veiculoId,
                     @RequestBody(content = @Content(examples = @ExampleObject(value = """
                             {"tipo": "CORRETIVA", "descricao": "Troca das pastilhas de freio",
                              "dataInicio": "2026-10-28", "dataPrevistaFim": "2026-11-03"}
                             """))) Manutencao manutencao);

    @Operation(summary = "Encerra uma manutenção",
            description = "Informa data de fim e custo (≥ 0); o veículo volta a DISPONIVEL (RN13).")
    @ApiResponse(responseCode = "200", description = "Manutenção encerrada")
    @ApiResponse(responseCode = "404", description = "Manutenção não encontrada")
    @ApiResponse(responseCode = "422", description = "Já encerrada, custo ausente/negativo ou data de fim inválida")
    Manutencao encerrar(@Parameter(description = "Id da manutenção", example = "2") Long id,
                        @RequestBody(content = @Content(examples = @ExampleObject(
                                value = "{\"dataFim\": \"2026-11-03\", \"custo\": 850.00}"))) Manutencao dados);
}
