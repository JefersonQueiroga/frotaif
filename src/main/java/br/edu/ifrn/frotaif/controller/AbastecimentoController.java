package br.edu.ifrn.frotaif.controller;

import br.edu.ifrn.frotaif.controller.doc.AbastecimentoControllerDoc;
import br.edu.ifrn.frotaif.dto.abastecimento.AbastecimentoRequest;
import br.edu.ifrn.frotaif.dto.abastecimento.AbastecimentoResponse;
import br.edu.ifrn.frotaif.service.AbastecimentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Recurso aninhado: abastecimento só existe dentro de um veículo
@RestController
@RequestMapping("/api/veiculos/{veiculoId}/abastecimentos")
public class AbastecimentoController implements AbastecimentoControllerDoc {

    private final AbastecimentoService abastecimentoService;

    public AbastecimentoController(AbastecimentoService abastecimentoService) {
        this.abastecimentoService = abastecimentoService;
    }

    @GetMapping
    public List<AbastecimentoResponse> listar(@PathVariable Long veiculoId) {
        return abastecimentoService.listar(veiculoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AbastecimentoResponse registrar(@PathVariable Long veiculoId, @RequestBody @Valid AbastecimentoRequest request) {
        return abastecimentoService.registrar(veiculoId, request);
    }
}
