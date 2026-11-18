package br.edu.ifrn.frotaif.controller;

import br.edu.ifrn.frotaif.controller.doc.ViagemControllerDoc;
import br.edu.ifrn.frotaif.dto.viagem.AprovarViagemRequest;
import br.edu.ifrn.frotaif.dto.viagem.ConcluirViagemRequest;
import br.edu.ifrn.frotaif.dto.viagem.MotivoRequest;
import br.edu.ifrn.frotaif.dto.viagem.SolicitarViagemRequest;
import br.edu.ifrn.frotaif.dto.viagem.ViagemResponse;
import br.edu.ifrn.frotaif.model.Usuario;
import br.edu.ifrn.frotaif.model.enums.StatusViagem;
import br.edu.ifrn.frotaif.service.ViagemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/viagens")
public class ViagemController implements ViagemControllerDoc {

    private final ViagemService viagemService;

    public ViagemController(ViagemService viagemService) {
        this.viagemService = viagemService;
    }

    // Ex.: /api/viagens?status=SOLICITADA&inicio=2026-11-01&fim=2026-11-30&solicitanteId=2
    @GetMapping
    public List<ViagemResponse> listar(@RequestParam(required = false) StatusViagem status,
                               @RequestParam(required = false) Long solicitanteId,
                               @RequestParam(required = false) LocalDate inicio,
                               @RequestParam(required = false) LocalDate fim) {
        return viagemService.listar(status, solicitanteId, inicio, fim);
    }

    @GetMapping("/{id}")
    public ViagemResponse buscarPorId(@PathVariable Long id) {
        return viagemService.buscarPorId(id);
    }

    // O solicitante é o usuário logado: o cliente não escolhe mais em nome de quem pede
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ViagemResponse solicitar(@RequestBody @Valid SolicitarViagemRequest request,
                                    @AuthenticationPrincipal Usuario usuario) {
        return viagemService.solicitar(request, usuario.getId());
    }

    // Corpo: {"veiculoId": 1, "motoristaId": 1}
    @PatchMapping("/{id}/aprovar")
    public ViagemResponse aprovar(@PathVariable Long id, @RequestBody AprovarViagemRequest request) {
        return viagemService.aprovar(id, request);
    }

    // Corpo: {"motivo": "..."}
    @PatchMapping("/{id}/rejeitar")
    public ViagemResponse rejeitar(@PathVariable Long id, @RequestBody MotivoRequest request) {
        return viagemService.rejeitar(id, request);
    }

    // @AuthenticationPrincipal: o Usuario que o Spring Security autenticou (antes vinha do header X-Usuario-Id,
    // que o próprio cliente preenchia)
    @PatchMapping("/{id}/iniciar")
    public ViagemResponse iniciar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return viagemService.iniciar(id, usuario.getId());
    }

    // Corpo: {"kmFinal": 35400}
    @PatchMapping("/{id}/concluir")
    public ViagemResponse concluir(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario,
                           @RequestBody ConcluirViagemRequest request) {
        return viagemService.concluir(id, usuario.getId(), request);
    }

    // Corpo: {"motivo": "..."}
    @PatchMapping("/{id}/cancelar")
    public ViagemResponse cancelar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario,
                           @RequestBody MotivoRequest request) {
        return viagemService.cancelar(id, usuario.getId(), request);
    }
}
