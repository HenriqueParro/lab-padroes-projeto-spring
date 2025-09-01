package one.digitalinnovation.gof.controller;

import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints auxiliares para trabalhar com Endereço/CEP.
 * - Consultar no ViaCEP (sem salvar).
 * - Buscar do banco (se já persistido).
 * - Sincronizar: consultar no ViaCEP e persistir no BD.
 */
@RestController
@RequestMapping("enderecos")
public class EnderecoRestController {

    @Autowired
    private ClienteService clienteService;

    // GET /enderecos/lookup/{cep}  (consulta ViaCEP, não salva)
    @GetMapping("/lookup/{cep}")
    public ResponseEntity<Endereco> consultarViaCep(@PathVariable String cep) {
        Endereco e = clienteService.consultarEnderecoViaCep(cep);
        return ResponseEntity.ok(e);
    }

    // GET /enderecos/{cep}  (busca no banco)
    @GetMapping("/{cep}")
    public ResponseEntity<Endereco> buscarNoBanco(@PathVariable String cep) {
        Endereco e = clienteService.buscarEnderecoNoBanco(cep);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(e);
    }

    // POST /enderecos/sync/{cep}  (consulta ViaCEP e PERSISTE no BD)
    @PostMapping("/sync/{cep}")
    public ResponseEntity<Endereco> sincronizar(@PathVariable String cep) {
        Endereco salvo = clienteService.sincronizarEnderecoPorCep(cep);
        return ResponseEntity.ok(salvo);
    }
}
