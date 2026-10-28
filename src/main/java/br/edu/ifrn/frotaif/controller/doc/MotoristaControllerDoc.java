package br.edu.ifrn.frotaif.controller.doc;

import br.edu.ifrn.frotaif.model.Motorista;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

// Documentação da API (Swagger) do MotoristaController — o controller só implementa esta interface
@Tag(name = "Motoristas", description = "Cadastro de motoristas terceirizados (RF03, RF04)")
public interface MotoristaControllerDoc {

    @Operation(summary = "Lista motoristas ativos", description = "Os inativos ficam de fora (RN02).")
    List<Motorista> listar();

    @Operation(summary = "Busca um motorista pelo id")
    @ApiResponse(responseCode = "200", description = "Motorista encontrado")
    @ApiResponse(responseCode = "404", description = "Motorista não encontrado")
    Motorista buscarPorId(@Parameter(description = "Id do motorista", example = "1") Long id);

    @Operation(summary = "Lista motoristas com CNH vencendo",
            description = "Motoristas ativos cuja CNH vence de hoje até hoje + N dias (RF04). CNHs já vencidas não aparecem.")
    @ApiResponse(responseCode = "200", description = "Lista ordenada pela validade da CNH")
    @ApiResponse(responseCode = "422", description = "Quantidade de dias negativa")
    List<Motorista> listarComCnhVencendo(@Parameter(description = "Janela em dias", example = "30") int dias);

    @Operation(summary = "Cadastra um motorista",
            description = "CPF com dígito verificador válido; CPF e registro da CNH únicos (RN03). "
                    + "O vínculo com o usuário de login entra na aula de Security.")
    @ApiResponse(responseCode = "201", description = "Motorista cadastrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos (ex.: CPF inválido)")
    @ApiResponse(responseCode = "409", description = "CPF ou CNH já cadastrados (RN03)")
    Motorista cadastrar(Motorista motorista);

    @Operation(summary = "Edita um motorista")
    @ApiResponse(responseCode = "200", description = "Motorista atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Motorista não encontrado")
    @ApiResponse(responseCode = "409", description = "CPF ou CNH pertencem a outro motorista (RN03)")
    Motorista atualizar(@Parameter(description = "Id do motorista", example = "1") Long id, Motorista motorista);

    @Operation(summary = "Inativa um motorista", description = "Exclusão lógica (RN02).")
    @ApiResponse(responseCode = "204", description = "Motorista inativado")
    @ApiResponse(responseCode = "404", description = "Motorista não encontrado")
    void inativar(@Parameter(description = "Id do motorista", example = "1") Long id);
}
