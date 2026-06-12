package br.com.katidantas.smartdelivery.cardapio;

import br.com.katidantas.smartdelivery.restaurante.RestauranteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static br.com.katidantas.smartdelivery.cardapio.CardapioController.RESTAURANTE_ID_CARDAPIO_PATH;

@RestController
@RequestMapping(RESTAURANTE_ID_CARDAPIO_PATH)
public class CardapioController {


    private final CardapioService cardapioService;
    private final RestauranteService restauranteService;
    static final String RESTAURANTE_ID_CARDAPIO_PATH = "/restaurantes/{restauranteId}/cardapio";

    public CardapioController(CardapioService cardapioService, RestauranteService restauranteService) {
        this.cardapioService = cardapioService;
        this.restauranteService = restauranteService;
    }

    @PostMapping()
    public ResponseEntity<DadosDetalhamentoCardapioItemDTO> criarCadastro(@PathVariable Long restauranteId, @RequestBody @Valid DadosCardapioItemDTO dadosCardapioItemDTO, UriComponentsBuilder uriComponentsBuilder) {
        CardapioItem item = dadosCardapioItemDTO.toEntity();

        CardapioItem itemSalvo = cardapioService.save(restauranteId, item);
        URI uri = uriComponentsBuilder.path(RESTAURANTE_ID_CARDAPIO_PATH)
                .buildAndExpand(itemSalvo.getId())
                .toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoCardapioItemDTO(itemSalvo));
    }

    @GetMapping("/{itemId}")
    ResponseEntity<DadosDetalhamentoCardapioItemDTO> buscarCardapio(@PathVariable Long restauranteId, @PathVariable Long itemId) {

        CardapioItem cardapioItem = cardapioService.buscarItemDoCardapio(restauranteId, itemId);

        return ResponseEntity.ok(new DadosDetalhamentoCardapioItemDTO(cardapioItem));

    }

    @GetMapping()
    ResponseEntity<Page<DadosListaItensDoCardapioDTO>> buscaTodosOsItensDoCardapio(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao, @PathVariable Long restauranteId) {
        Page<DadosListaItensDoCardapioDTO> page = cardapioService.buscarTodosOsItensDoCardapio(restauranteId, paginacao).map(DadosListaItensDoCardapioDTO::new);
        return ResponseEntity.ok(page);
    }

    @PatchMapping("/{itemId}")
    ResponseEntity<DadosDetalhamentoCardapioItemDTO> atualizarItens(@PathVariable Long restauranteId, @PathVariable Long itemId, @RequestBody @Valid DadosAtualizacaoCardapioItemDTO dadosAtualizacaoCardapioItemDTO) {
        CardapioItem cardapioItem = dadosAtualizacaoCardapioItemDTO.toEntity();

        CardapioItem itemAtualizado = cardapioService.atualizarItemDoCardapio(restauranteId, itemId, cardapioItem);

        return ResponseEntity.ok(new DadosDetalhamentoCardapioItemDTO(itemAtualizado));
    }

    @DeleteMapping("/{itemId}")
    ResponseEntity<Void> inativarItemDoCardapio(@PathVariable Long restauranteId, @PathVariable Long itemId) {
        cardapioService.inativarItemDoCardapio(restauranteId, itemId);
        return ResponseEntity.noContent().build();
    }
}
