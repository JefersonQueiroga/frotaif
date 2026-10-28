package br.edu.ifrn.frotaif.controller.doc;

import br.edu.ifrn.frotaif.model.Abastecimento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

// Documentação da API (Swagger) do AbastecimentoController — o controller só implementa esta interface
@Tag(name = "Abastecimentos", description = "Abastecimentos de um veículo (RF10)")
public interface AbastecimentoControllerDoc {

    @Operation(summary = "Lista os abastecimentos de um veículo", description = "Do mais recente para o mais antigo.")
    @ApiResponse(responseCode = "200", description = "Lista de abastecimentos")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    List<Abastecimento> listar(@Parameter(description = "Id do veículo", example = "1") Long veiculoId);

    @Operation(summary = "Registra um abastecimento",
            description = "O km informado não pode ser menor que o último conhecido do veículo; "
                    + "se for maior, atualiza a quilometragem do veículo (RN11).")
    @ApiResponse(responseCode = "201", description = "Abastecimento registrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos (ex.: data futura, litros ≤ 0)")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    @ApiResponse(responseCode = "422", description = "Km menor que o último conhecido (RN11)")
    Abastecimento registrar(@Parameter(description = "Id do veículo", example = "1") Long veiculoId,
                            @RequestBody(content = @Content(examples = @ExampleObject(value = """
                                    {"data": "2026-10-28", "litros": 42.3, "valorTotal": 266.49,
                                     "kmNoAbastecimento": 35600, "posto": "Posto Central"}
                                    """))) Abastecimento abastecimento);
}
