package br.com.katidantas.smartdelivery.cardapio;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static br.com.katidantas.smartdelivery.cardapio.CardapioController.RESTAURANTE_ID_CARDAPIO_PATH;

@RestController
@RequestMapping(RESTAURANTE_ID_CARDAPIO_PATH)
public class CardapioController {


    private final CardapioService cardapioService;
    static final String RESTAURANTE_ID_CARDAPIO_PATH = "/restaurantes/{restauranteId}/cardapio";

    public CardapioController(CardapioService cardapioService) {
        this.cardapioService = cardapioService;
    }

    @PostMapping()
    public ResponseEntity<DadosDetalhamentoCardapioItemDTO> criarCadastro(@PathVariable Long restauranteId, @RequestBody @Valid DadosCardapioItemDTO dadosCardapioItemDTO, UriComponentsBuilder uriComponentsBuilder) {
        CardapioItem item = dadosCardapioItemDTO.toEntity();

        cardapioService.save(restauranteId, item);
        URI uri = uriComponentsBuilder.path(RESTAURANTE_ID_CARDAPIO_PATH)
                .buildAndExpand(item.getId())
                .toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoCardapioItemDTO(item));
    }


}
