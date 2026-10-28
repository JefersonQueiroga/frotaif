package br.edu.ifrn.frotaif.controller.doc;

import br.edu.ifrn.frotaif.model.Veiculo;
import br.edu.ifrn.frotaif.model.enums.StatusVeiculo;
import br.edu.ifrn.frotaif.model.enums.TipoVeiculo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

// Documentação da API (Swagger) do VeiculoController — o controller só implementa esta interface
@Tag(name = "Veículos", description = "Cadastro da frota (RF01, RF02)")
public interface VeiculoControllerDoc {

    @Operation(summary = "Lista veículos",
            description = "Sem filtro de status, os veículos INATIVOS ficam de fora (RN02).")
    List<Veiculo> listar(
            @Parameter(description = "Filtra por status. Use INATIVO para ver os excluídos") StatusVeiculo status,
            @Parameter(description = "Filtra por tipo") TipoVeiculo tipo);

    @Operation(summary = "Busca um veículo pelo id", description = "Retorna inclusive veículos inativos.")
    @ApiResponse(responseCode = "200", description = "Veículo encontrado")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    Veiculo buscarPorId(@Parameter(description = "Id do veículo", example = "1") Long id);

    @Operation(summary = "Cadastra um veículo",
            description = "O veículo começa DISPONIVEL e com a quilometragem informada (ou 0). "
                    + "A placa deve ser única e no formato Mercosul AAA0A00 (RN01).")
    @ApiResponse(responseCode = "201", description = "Veículo cadastrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos (ex.: placa fora do formato Mercosul)")
    @ApiResponse(responseCode = "409", description = "Já existe veículo com essa placa (RN01)")
    Veiculo cadastrar(Veiculo veiculo);

    @Operation(summary = "Edita um veículo",
            description = "Não altera status nem quilometragem: eles mudam pelas viagens, abastecimentos e manutenções.")
    @ApiResponse(responseCode = "200", description = "Veículo atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    @ApiResponse(responseCode = "409", description = "A placa já pertence a outro veículo (RN01)")
    Veiculo atualizar(@Parameter(description = "Id do veículo", example = "1") Long id, Veiculo veiculo);

    @Operation(summary = "Inativa um veículo", description = "Exclusão lógica: o veículo passa a INATIVO (RN02).")
    @ApiResponse(responseCode = "204", description = "Veículo inativado")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    void inativar(@Parameter(description = "Id do veículo", example = "1") Long id);

    @Operation(summary = "Reativa um veículo inativo", description = "O veículo volta a DISPONIVEL (RF02).")
    @ApiResponse(responseCode = "200", description = "Veículo reativado")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    @ApiResponse(responseCode = "422", description = "O veículo não está inativo")
    Veiculo reativar(@Parameter(description = "Id do veículo", example = "5") Long id);
}
