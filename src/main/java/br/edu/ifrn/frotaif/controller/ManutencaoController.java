package br.edu.ifrn.frotaif.controller;

import br.edu.ifrn.frotaif.controller.doc.ManutencaoControllerDoc;
import br.edu.ifrn.frotaif.dto.manutencao.AbrirManutencaoRequest;
import br.edu.ifrn.frotaif.dto.manutencao.EncerrarManutencaoRequest;
import br.edu.ifrn.frotaif.dto.manutencao.ManutencaoResponse;
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
    public List<ManutencaoResponse> listar(@PathVariable Long veiculoId) {
        return manutencaoService.listar(veiculoId);
    }

    @PostMapping("/veiculos/{veiculoId}/manutencoes")
    @ResponseStatus(HttpStatus.CREATED)
    public ManutencaoResponse abrir(@PathVariable Long veiculoId, @RequestBody @Valid AbrirManutencaoRequest request) {
        return manutencaoService.abrir(veiculoId, request);
    }

    // Corpo: {"dataFim": "2026-11-10", "custo": 850.00}
    @PatchMapping("/manutencoes/{id}/encerrar")
    public ManutencaoResponse encerrar(@PathVariable Long id, @RequestBody EncerrarManutencaoRequest request) {
        return manutencaoService.encerrar(id, request);
    }
}
