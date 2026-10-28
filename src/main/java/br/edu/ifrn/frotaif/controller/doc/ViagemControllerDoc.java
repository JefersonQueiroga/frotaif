package br.edu.ifrn.frotaif.controller.doc;

import br.edu.ifrn.frotaif.model.Viagem;
import br.edu.ifrn.frotaif.model.enums.StatusViagem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;

// Documentação da API (Swagger) do ViagemController — o controller só implementa esta interface
@Tag(name = "Viagens", description = "Solicitação, aprovação e execução de viagens (RF05–RF09)")
public interface ViagemControllerDoc {

    String USUARIO = "Id do usuário que está agindo (simula o login até a aula de Security)";

    @Operation(summary = "Lista viagens", description = "Todos os filtros são opcionais e podem ser combinados (RF09). "
            + "O período é filtrado pela data de saída.")
    @ApiResponse(responseCode = "200", description = "Viagens ordenadas pela saída")
    @ApiResponse(responseCode = "422", description = "Data final anterior à inicial")
    List<Viagem> listar(
            @Parameter(description = "Filtra por status") StatusViagem status,
            @Parameter(description = "Filtra pelo id do solicitante", example = "2") Long solicitanteId,
            @Parameter(description = "Saída a partir deste dia (aaaa-mm-dd)") LocalDate inicio,
            @Parameter(description = "Saída até este dia, inclusive (aaaa-mm-dd)") LocalDate fim);

    @Operation(summary = "Busca uma viagem pelo id")
    @ApiResponse(responseCode = "200", description = "Viagem encontrada")
    @ApiResponse(responseCode = "404", description = "Viagem não encontrada")
    Viagem buscarPorId(@Parameter(description = "Id da viagem", example = "1") Long id);

    @Operation(summary = "Solicita uma viagem",
            description = "A viagem nasce SOLICITADA. A saída precisa respeitar a antecedência mínima configurada "
                    + "em frota.viagem.antecedencia-minima-horas (RN04) e o retorno deve ser posterior à saída.")
    @ApiResponse(responseCode = "201", description = "Viagem solicitada")
    @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes ou inválidos")
    @ApiResponse(responseCode = "404", description = "Solicitante não encontrado")
    @ApiResponse(responseCode = "422", description = "Antecedência insuficiente, retorno antes da saída ou solicitante inativo")
    Viagem solicitar(@RequestBody(content = @Content(examples = @ExampleObject(value = """
            {"solicitante": {"id": 2}, "destino": "Natal", "finalidade": "Reunião na Reitoria",
             "saida": "2030-01-10T07:00:00", "retornoPrevisto": "2030-01-10T18:00:00", "qtdPassageiros": 3}
            """))) Viagem viagem);

    @Operation(summary = "Aprova uma viagem alocando veículo e motorista (RF06)",
            description = "Verifica capacidade (RN05), veículo disponível e sem conflito de período (RN06), "
                    + "motorista ativo e sem conflito (RN07) e CNH válida e de categoria compatível (RN08).")
    @ApiResponse(responseCode = "200", description = "Viagem APROVADA")
    @ApiResponse(responseCode = "404", description = "Viagem, veículo ou motorista não encontrados")
    @ApiResponse(responseCode = "409", description = "Veículo indisponível ou com conflito; motorista inativo ou com conflito")
    @ApiResponse(responseCode = "422", description = "Viagem não está SOLICITADA, capacidade insuficiente ou CNH inválida")
    Viagem aprovar(@Parameter(description = "Id da viagem", example = "1") Long id,
                   @RequestBody(content = @Content(examples = @ExampleObject(
                           value = "{\"veiculo\": {\"id\": 2}, \"motorista\": {\"id\": 2}}"))) Viagem dados);

    @Operation(summary = "Rejeita uma viagem", description = "Só viagens SOLICITADAS; o motivo é obrigatório.")
    @ApiResponse(responseCode = "200", description = "Viagem REJEITADA")
    @ApiResponse(responseCode = "404", description = "Viagem não encontrada")
    @ApiResponse(responseCode = "422", description = "Viagem não está SOLICITADA ou motivo ausente")
    Viagem rejeitar(@Parameter(description = "Id da viagem", example = "3") Long id,
                    @RequestBody(content = @Content(examples = @ExampleObject(
                            value = "{\"motivo\": \"Sem orçamento\"}"))) Viagem dados);

    @Operation(summary = "Inicia uma viagem aprovada (RF07)",
            description = "Só o motorista alocado (RN09). Revalida a CNH (RN08), registra o km inicial "
                    + "e o veículo passa a EM_VIAGEM (RN10).")
    @ApiResponse(responseCode = "200", description = "Viagem EM_ANDAMENTO")
    @ApiResponse(responseCode = "403", description = "Usuário não é o motorista alocado")
    @ApiResponse(responseCode = "409", description = "Veículo não está disponível")
    @ApiResponse(responseCode = "422", description = "Viagem não está APROVADA ou CNH inválida")
    Viagem iniciar(@Parameter(description = "Id da viagem", example = "4") Long id,
                   @Parameter(description = USUARIO, example = "4") Long usuarioId);

    @Operation(summary = "Conclui uma viagem em andamento (RF07)",
            description = "Só o motorista alocado (RN09). O km final deve ser maior que o inicial; "
                    + "atualiza a quilometragem do veículo, que volta a DISPONIVEL (RN10).")
    @ApiResponse(responseCode = "200", description = "Viagem CONCLUIDA")
    @ApiResponse(responseCode = "403", description = "Usuário não é o motorista alocado")
    @ApiResponse(responseCode = "422", description = "Viagem não está EM_ANDAMENTO ou km final inválido")
    Viagem concluir(@Parameter(description = "Id da viagem", example = "4") Long id,
                    @Parameter(description = USUARIO, example = "4") Long usuarioId,
                    @RequestBody(content = @Content(examples = @ExampleObject(
                            value = "{\"kmFinal\": 35400}"))) Viagem dados);

    @Operation(summary = "Cancela uma viagem (RF08)",
            description = "O solicitante cancela só as próprias viagens; o gestor, qualquer uma. "
                    + "Apenas enquanto SOLICITADA ou APROVADA, com motivo (RN14).")
    @ApiResponse(responseCode = "200", description = "Viagem CANCELADA")
    @ApiResponse(responseCode = "403", description = "Usuário não é o solicitante nem gestor")
    @ApiResponse(responseCode = "404", description = "Viagem ou usuário não encontrados")
    @ApiResponse(responseCode = "422", description = "Viagem não pode mais ser cancelada ou motivo ausente")
    Viagem cancelar(@Parameter(description = "Id da viagem", example = "1") Long id,
                    @Parameter(description = USUARIO, example = "2") Long usuarioId,
                    @RequestBody(content = @Content(examples = @ExampleObject(
                            value = "{\"motivo\": \"Reunião adiada\"}"))) Viagem dados);
}
