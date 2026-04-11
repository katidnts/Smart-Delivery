package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.restaurante.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("restaurantes")
public class RestauranteController {

    @Autowired
    private RestauranteService service;

    @PostMapping
    public ResponseEntity criar(@RequestBody @Valid DadosRestaurante dadosRestaurante, UriComponentsBuilder uriBuilder) {
        Restaurante restaurante = dadosRestaurante.toEntity();
        service.save(restaurante);
        URI uri = uriBuilder.path("/restaurantes/{id}")
                .buildAndExpand(restaurante.getId())
                .toUri();

        return ResponseEntity.created(uri)
                .body(new DadosDetalhamentoRestaurante(restaurante));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoRestaurante> buscarPorId(@PathVariable Long id) {
        return service.buscarRestaurantePorId(id)
                .map(restaurante -> ResponseEntity.ok(new DadosDetalhamentoRestaurante(restaurante)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity atualizarCampos(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoRestauranteDTO dadosAtualizacaoRestaurante) {

        Restaurante restaurante = dadosAtualizacaoRestaurante.toEntity();

        Restaurante restauranteAtualizado = service.atualizarCampos(id, restaurante);

        return ResponseEntity.ok(new DadosDetalhamentoRestaurante(restauranteAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity inativarRestaurante(@PathVariable Long id) {
        Restaurante restaurante = service.inativar(id);

        return ResponseEntity.noContent().build();
    }
}

