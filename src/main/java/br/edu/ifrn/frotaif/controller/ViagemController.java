package br.edu.ifrn.frotaif.controller;

import br.edu.ifrn.frotaif.model.Viagem;
import br.edu.ifrn.frotaif.model.enums.StatusViagem;
import br.edu.ifrn.frotaif.service.ViagemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/viagens")
public class ViagemController {

    private final ViagemService viagemService;

    public ViagemController(ViagemService viagemService) {
        this.viagemService = viagemService;
    }

    // Ex.: /api/viagens?status=SOLICITADA&inicio=2026-11-01&fim=2026-11-30&solicitanteId=2
    @GetMapping
    public List<Viagem> listar(@RequestParam(required = false) StatusViagem status,
                               @RequestParam(required = false) Long solicitanteId,
                               @RequestParam(required = false) LocalDate inicio,
                               @RequestParam(required = false) LocalDate fim) {
        return viagemService.listar(status, solicitanteId, inicio, fim);
    }

    @GetMapping("/{id}")
    public Viagem buscarPorId(@PathVariable Long id) {
        return viagemService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Viagem solicitar(@RequestBody @Valid Viagem viagem) {
        return viagemService.solicitar(viagem);
    }

    // Corpo: {"veiculo": {"id": 1}, "motorista": {"id": 1}}
    @PatchMapping("/{id}/aprovar")
    public Viagem aprovar(@PathVariable Long id, @RequestBody Viagem dados) {
        return viagemService.aprovar(id, dados);
    }

    // Corpo: {"motivo": "..."}
    @PatchMapping("/{id}/rejeitar")
    public Viagem rejeitar(@PathVariable Long id, @RequestBody Viagem dados) {
        return viagemService.rejeitar(id, dados);
    }
}
