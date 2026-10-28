package br.edu.ifrn.frotaif.controller;

import br.edu.ifrn.frotaif.model.Abastecimento;
import br.edu.ifrn.frotaif.service.AbastecimentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Recurso aninhado: abastecimento só existe dentro de um veículo
@RestController
@RequestMapping("/api/veiculos/{veiculoId}/abastecimentos")
public class AbastecimentoController {

    private final AbastecimentoService abastecimentoService;

    public AbastecimentoController(AbastecimentoService abastecimentoService) {
        this.abastecimentoService = abastecimentoService;
    }

    @GetMapping
    public List<Abastecimento> listar(@PathVariable Long veiculoId) {
        return abastecimentoService.listar(veiculoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Abastecimento registrar(@PathVariable Long veiculoId, @RequestBody @Valid Abastecimento abastecimento) {
        return abastecimentoService.registrar(veiculoId, abastecimento);
    }
}
