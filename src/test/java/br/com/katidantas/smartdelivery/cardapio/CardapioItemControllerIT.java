package br.com.katidantas.smartdelivery.cardapio;

import br.com.katidantas.smartdelivery.endereco.Endereco;
import br.com.katidantas.smartdelivery.restaurante.Restaurante;
import br.com.katidantas.smartdelivery.restaurante.RestauranteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CardapioItemControllerIT {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private CardapioRepository cardapioRepository;

    private Restaurante restaurante;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.create("http://localhost:" + port);
        restaurante = restauranteRepository.save(criaRestaurante());
    }

    @AfterEach
    void limparBanco() {
        cardapioRepository.deleteAll();
        restauranteRepository.deleteAll();
    }

    @Test
    void deveCriarCardapio() {
        //Given
        DadosCardapioItemDTO itemCardapio = criaCardapioDTO();

        //When
        ResponseEntity<DadosDetalhamentoCardapioItemDTO> response = restClient.post()
                .uri("/restaurantes/{restauranteId}/cardapio", restaurante.getId())
                .body(itemCardapio)
                .retrieve()
                .toEntity(DadosDetalhamentoCardapioItemDTO.class);

        DadosDetalhamentoCardapioItemDTO dadosDetalhamentoCardapioItemDTO = response.getBody();
        int statusCode = response.getStatusCode().value();

        //Then
        assertThat(statusCode).isEqualTo(201);
        assertThat(dadosDetalhamentoCardapioItemDTO.id()).isNotNull();
        assertThat(dadosDetalhamentoCardapioItemDTO.ativo()).isTrue();
        assertThat(dadosDetalhamentoCardapioItemDTO.nome()).isEqualTo(itemCardapio.nome());
        assertThat(dadosDetalhamentoCardapioItemDTO.descricao()).isEqualTo(itemCardapio.descricao());
        assertThat(dadosDetalhamentoCardapioItemDTO.categoria()).isEqualTo(itemCardapio.categoria());
        assertThat(dadosDetalhamentoCardapioItemDTO.preco()).isEqualTo(itemCardapio.preco());
        assertThat(dadosDetalhamentoCardapioItemDTO.fotoUrl()).isEqualTo(itemCardapio.fotoUrl());

    }

    @Test
    void deveBuscarCardapioItemPorId() {
        //Given
        CardapioItem cardapioItem = criaCardapioItemMock();
        cardapioItem.setRestaurante(restaurante);
        CardapioItem itemSalvo = cardapioRepository.save(cardapioItem);

        //When
        ResponseEntity<DadosDetalhamentoCardapioItemDTO> dadosDetalhamentoCardapioItemDTOResponse = restClient.get()
                .uri("/restaurantes/{restauranteId}/cardapio/{cardapioItemId}", restaurante.getId(), itemSalvo.getId())
                .retrieve()
                .toEntity(DadosDetalhamentoCardapioItemDTO.class);

        DadosDetalhamentoCardapioItemDTO dadosDetalhamentoCardapioItemDTO = dadosDetalhamentoCardapioItemDTOResponse.getBody();
        int statusCode = dadosDetalhamentoCardapioItemDTOResponse.getStatusCode().value();

        //Then
        assertThat(statusCode).isEqualTo(200);
        assertThat(dadosDetalhamentoCardapioItemDTO.id()).isNotNull();
        assertThat(dadosDetalhamentoCardapioItemDTO.ativo()).isEqualTo(itemSalvo.getAtivo());
        assertThat(dadosDetalhamentoCardapioItemDTO.nome()).isEqualTo(itemSalvo.getNome());
        assertThat(dadosDetalhamentoCardapioItemDTO.descricao()).isEqualTo(itemSalvo.getDescricao());
        assertThat(dadosDetalhamentoCardapioItemDTO.categoria()).isEqualTo(itemSalvo.getCategoria());
        assertThat(dadosDetalhamentoCardapioItemDTO.fotoUrl()).isEqualTo(itemSalvo.getFotoUrl());
        assertThat(dadosDetalhamentoCardapioItemDTO.preco()).isEqualTo(itemSalvo.getPreco());

    }

    private CardapioItem criaCardapioItemMock() {
        CardapioItem cardapioItem = new CardapioItem();
        cardapioItem.setNome("Escondidinho de carne seca");
        cardapioItem.setCategoria(CategoriaItem.PRATO_INDIVIDUAL);
        cardapioItem.setDescricao("Delicioso prato acompanhado de purê de abobóra");
        cardapioItem.setPreco(new BigDecimal("59.90"));
        cardapioItem.setAtivo(true);
        cardapioItem.setFotoUrl(null);
        return cardapioItem;
    }

    private DadosCardapioItemDTO criaCardapioDTO() {
        return new DadosCardapioItemDTO(
                "Escondidinho de carne seca",
                "Delicioso prato acompanhado de purê de abobóra",
                CategoriaItem.PRATO_INDIVIDUAL,
                new BigDecimal("59.90"),
                null
        );
    }

    private static Restaurante criaRestaurante() {
        Endereco endereco = new Endereco(
                null,
                "22220001",
                "Rua do Catete",
                "200", "315",
                "Catete",
                "Rio de Janeiro",
                "RJ");

        Restaurante restaurante = new Restaurante();
        restaurante.setId(null);
        restaurante.setNome("Bar da onça");
        restaurante.setCnpj("11222333000181");
        restaurante.setTelefone("999999999");
        restaurante.setEndereco(endereco);
        restaurante.setAtivo(true);
        restaurante.setCardapio(null);

        return restaurante;

    }
}
