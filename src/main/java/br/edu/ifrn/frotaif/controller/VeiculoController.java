package br.edu.ifrn.frotaif.controller;

import br.edu.ifrn.frotaif.controller.doc.VeiculoControllerDoc;
import br.edu.ifrn.frotaif.dto.veiculo.VeiculoRequest;
import br.edu.ifrn.frotaif.dto.veiculo.VeiculoResponse;
import br.edu.ifrn.frotaif.model.enums.StatusVeiculo;
import br.edu.ifrn.frotaif.model.enums.TipoVeiculo;
import br.edu.ifrn.frotaif.service.VeiculoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController implements VeiculoControllerDoc {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @GetMapping
    public List<VeiculoResponse> listar(@RequestParam(required = false) StatusVeiculo status,
                                @RequestParam(required = false) TipoVeiculo tipo) {
        return veiculoService.listar(status, tipo);
    }

    @GetMapping("/{id}")
    public VeiculoResponse buscarPorId(@PathVariable Long id) {
        return veiculoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VeiculoResponse cadastrar(@RequestBody @Valid VeiculoRequest request) {
        return veiculoService.cadastrar(request);
    }

    @PutMapping("/{id}")
    public VeiculoResponse atualizar(@PathVariable Long id, @RequestBody @Valid VeiculoRequest request) {
        return veiculoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativar(@PathVariable Long id) {
        veiculoService.inativar(id);
    }

    @PatchMapping("/{id}/reativar")
    public VeiculoResponse reativar(@PathVariable Long id) {
        return veiculoService.reativar(id);
    }
}
