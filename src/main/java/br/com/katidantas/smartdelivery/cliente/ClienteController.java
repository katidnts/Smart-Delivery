package br.com.katidantas.smartdelivery.cliente;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping(ClienteController.CLIENTES_PATH)
public class ClienteController {

    static final String CLIENTES_PATH = "/clientes";
    private static final String ID_PARAMETER = "/{id}";
    private static final String CLIENTES_PATH_ID = CLIENTES_PATH + ID_PARAMETER;


    private final ClienteService service;

    @PostMapping
    public ResponseEntity<DadosDetalhamentoClienteDTO> criar(@RequestBody @Valid DadosClienteDTO dadosClienteDTO, UriComponentsBuilder uriBuilder) {
        Cliente cliente = service.save(dadosClienteDTO.toEntity());
        URI uri = uriBuilder.path(CLIENTES_PATH_ID)
                .buildAndExpand(cliente.getId())
                .toUri();
        return ResponseEntity.created(uri)
                .body(new DadosDetalhamentoClienteDTO(cliente));
    }
}
