package br.com.katidantas.smartdelivery.cliente;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping(ID_PARAMETER)
    public ResponseEntity<DadosDetalhamentoClienteDTO> buscarPorId(@PathVariable Long id) {

        Cliente cliente = service.buscarCliente(id);
        return ResponseEntity.ok(new DadosDetalhamentoClienteDTO(cliente));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListaClienteDTO>> buscarTodosClientesAtivos(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao) {
        Page<DadosListaClienteDTO> page = service.listarClientes(paginacao);
        return ResponseEntity.ok(page);
    }

    @PatchMapping(ID_PARAMETER)
    public ResponseEntity<DadosDetalhamentoClienteDTO> atualizarCliente(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoClienteDTO dadosAtualizacaoClienteDTO) {
        Cliente cliente = dadosAtualizacaoClienteDTO.toEntity();
        Cliente clienteAtualizado = service.atualizarCampos(id, cliente);
        return ResponseEntity.ok(new DadosDetalhamentoClienteDTO(clienteAtualizado));
    }

    @DeleteMapping(ID_PARAMETER)
    public ResponseEntity<Void> inativarCliente(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

}
