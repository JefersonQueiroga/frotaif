package br.edu.ifrn.frotaif.controller;

import br.edu.ifrn.frotaif.controller.doc.MotoristaControllerDoc;
import br.edu.ifrn.frotaif.dto.motorista.MotoristaRequest;
import br.edu.ifrn.frotaif.dto.motorista.MotoristaResponse;
import br.edu.ifrn.frotaif.service.MotoristaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/motoristas")
public class MotoristaController implements MotoristaControllerDoc {

    private final MotoristaService motoristaService;

    public MotoristaController(MotoristaService motoristaService) {
        this.motoristaService = motoristaService;
    }

    @GetMapping
    public List<MotoristaResponse> listar() {
        return motoristaService.listar();
    }

    @GetMapping("/{id}")
    public MotoristaResponse buscarPorId(@PathVariable Long id) {
        return motoristaService.buscarPorId(id);
    }

    @GetMapping("/cnh-vencendo")
    public List<MotoristaResponse> listarComCnhVencendo(@RequestParam(defaultValue = "30") int dias) {
        return motoristaService.listarComCnhVencendo(dias);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MotoristaResponse cadastrar(@RequestBody @Valid MotoristaRequest request) {
        return motoristaService.cadastrar(request);
    }

    @PutMapping("/{id}")
    public MotoristaResponse atualizar(@PathVariable Long id, @RequestBody @Valid MotoristaRequest request) {
        return motoristaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativar(@PathVariable Long id) {
        motoristaService.inativar(id);
    }
}
