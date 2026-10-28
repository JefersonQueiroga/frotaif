package br.edu.ifrn.frotaif.controller;

import br.edu.ifrn.frotaif.controller.doc.ManutencaoControllerDoc;
import br.edu.ifrn.frotaif.model.Manutencao;
import br.edu.ifrn.frotaif.service.ManutencaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ManutencaoController implements ManutencaoControllerDoc {

    private final ManutencaoService manutencaoService;

    public ManutencaoController(ManutencaoService manutencaoService) {
        this.manutencaoService = manutencaoService;
    }

    @GetMapping("/veiculos/{veiculoId}/manutencoes")
    public List<Manutencao> listar(@PathVariable Long veiculoId) {
        return manutencaoService.listar(veiculoId);
    }

    @PostMapping("/veiculos/{veiculoId}/manutencoes")
    @ResponseStatus(HttpStatus.CREATED)
    public Manutencao abrir(@PathVariable Long veiculoId, @RequestBody @Valid Manutencao manutencao) {
        return manutencaoService.abrir(veiculoId, manutencao);
    }

    // Corpo: {"dataFim": "2026-11-10", "custo": 850.00}
    @PatchMapping("/manutencoes/{id}/encerrar")
    public Manutencao encerrar(@PathVariable Long id, @RequestBody Manutencao dados) {
        return manutencaoService.encerrar(id, dados);
    }
}
