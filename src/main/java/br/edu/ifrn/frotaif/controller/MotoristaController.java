package br.edu.ifrn.frotaif.controller;

import br.edu.ifrn.frotaif.model.Motorista;
import br.edu.ifrn.frotaif.service.MotoristaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/motoristas")
public class MotoristaController {

    private final MotoristaService motoristaService;

    public MotoristaController(MotoristaService motoristaService) {
        this.motoristaService = motoristaService;
    }

    @GetMapping
    public List<Motorista> listar() {
        return motoristaService.listar();
    }

    @GetMapping("/{id}")
    public Motorista buscarPorId(@PathVariable Long id) {
        return motoristaService.buscarPorId(id);
    }

    @GetMapping("/cnh-vencendo")
    public List<Motorista> listarComCnhVencendo(@RequestParam(defaultValue = "30") int dias) {
        return motoristaService.listarComCnhVencendo(dias);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Motorista cadastrar(@RequestBody @Valid Motorista motorista) {
        return motoristaService.cadastrar(motorista);
    }

    @PutMapping("/{id}")
    public Motorista atualizar(@PathVariable Long id, @RequestBody @Valid Motorista motorista) {
        return motoristaService.atualizar(id, motorista);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativar(@PathVariable Long id) {
        motoristaService.inativar(id);
    }
}
