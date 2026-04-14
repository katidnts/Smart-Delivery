package br.com.katidantas.smartdelivery.restaurante;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static br.com.katidantas.smartdelivery.restaurante.RestauranteController.RESTAURANTES_PATH;

@AllArgsConstructor
@RestController
@RequestMapping(RESTAURANTES_PATH)
public class RestauranteController {

    static final String RESTAURANTES_PATH = "/restaurantes";
    private static final String ID_PARAMETER = "/{id}";
    private static final String RESTAURANTES_PATH_ID = RESTAURANTES_PATH + ID_PARAMETER;

    private final RestauranteService service;

    @PostMapping
    public ResponseEntity<DadosDetalhamentoRestauranteDTO> criar(@RequestBody @Valid DadosRestauranteDTO dadosRestauranteDTO, UriComponentsBuilder uriBuilder) {
        Restaurante restaurante = dadosRestauranteDTO.toEntity();
        service.save(restaurante);
        URI uri = uriBuilder.path(RESTAURANTES_PATH_ID)
                .buildAndExpand(restaurante.getId())
                .toUri();

        return ResponseEntity.created(uri)
                .body(new DadosDetalhamentoRestauranteDTO(restaurante));
    }

    @GetMapping(ID_PARAMETER)
    public ResponseEntity<DadosDetalhamentoRestauranteDTO> buscarPorId(@PathVariable Long id) {

        Restaurante restaurante = service.buscarRestaurantePorId(id);

        return ResponseEntity.ok(new DadosDetalhamentoRestauranteDTO(restaurante));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListaRestauranteDTO>> buscarTodosOsRestaurantesAtivos(@PageableDefault(size = 10, sort = {"nome"})Pageable paginacao) {
        Page<DadosListaRestauranteDTO> page = service.listarRestaurantes(paginacao).map(DadosListaRestauranteDTO::new);
        return ResponseEntity.ok(page);
    }

    @PatchMapping(ID_PARAMETER)
    public ResponseEntity<DadosDetalhamentoRestauranteDTO> atualizarCampos(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoRestauranteDTO dadosAtualizacaoRestaurante) {

        Restaurante restaurante = dadosAtualizacaoRestaurante.toEntity();

        Restaurante restauranteAtualizado = service.atualizarCampos(id, restaurante);

        return ResponseEntity.ok(new DadosDetalhamentoRestauranteDTO(restauranteAtualizado));
    }

    @DeleteMapping(ID_PARAMETER)
    public ResponseEntity<Void> inativarRestaurante(@PathVariable Long id) {

        service.inativar(id);

        return ResponseEntity.noContent().build();
    }
}

