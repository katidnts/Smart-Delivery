package br.com.katidantas.smartdelivery.endereco;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cep")
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService){
        this.cepService = cepService;
    }

    @GetMapping({"/cep"})
    public ResponseEntity<EnderecoParcialDTO> buscarCep(@PathVariable String cep) {
        return ResponseEntity.ok(cepService.buscarCep(cep));
    }
}
