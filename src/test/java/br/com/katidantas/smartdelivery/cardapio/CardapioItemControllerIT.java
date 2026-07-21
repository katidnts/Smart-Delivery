package br.com.katidantas.smartdelivery.cardapio;

import br.com.katidantas.smartdelivery.endereco.Endereco;
import br.com.katidantas.smartdelivery.restaurante.PageResponse;
import br.com.katidantas.smartdelivery.restaurante.Restaurante;
import br.com.katidantas.smartdelivery.restaurante.RestauranteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CardapioItemControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

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
    @DisplayName("Deve criar item do cardápio quando dados válidos")
    void deveCriarCardapio_QuandoDadosValidos() {
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
    @DisplayName("Deve buscar item do cardápio quando id válido")
    void deveBuscarCardapioItem_QuandoIdValido() {
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

    @Test
    @DisplayName("Deve buscar lista de itens ativos")
    void deveBuscarListaDeItens_QuandoAtivos() {
        //Given
        List<CardapioItem> itens = criaListaDeItensMock();
        itens.forEach(item -> item.setRestaurante(restaurante));
        cardapioRepository.saveAll(itens);

        //When
        ResponseEntity<PageResponse<DadosListaItensDoCardapioDTO>> dadosItensAtivos = restClient.get()
                .uri("/restaurantes/{restauranteId}/cardapio", restaurante.getId())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<PageResponse<DadosListaItensDoCardapioDTO>>() {
                });

        List<DadosListaItensDoCardapioDTO> itensResponse = dadosItensAtivos.getBody().content();

        //Then
        assertThat(dadosItensAtivos.getStatusCode().value()).isEqualTo(200);
        assertThat(itensResponse)
                .extracting(DadosListaItensDoCardapioDTO::nome)
                .containsExactlyInAnyOrderElementsOf(
                        itens.stream().filter(CardapioItem::getAtivo).map(CardapioItem::getNome).toList()
                );

    }

    @Test
    @DisplayName("Deve atualizar item do cardápio quando dados válidos")
    void deveAtualizarCardapioItem() {
        //Given
        CardapioItem cardapioItem = criaCardapioItemMock();
        cardapioItem.setRestaurante(restaurante);
        cardapioRepository.save(cardapioItem);

        DadosAtualizacaoCardapioItemDTO dadosAtualizacaoCardapioItemDTO = new DadosAtualizacaoCardapioItemDTO(
                "Novo nome",
                "Nova descrição",
                null,
                null,
                "foto-atualizada.jpg"
        );

        //When
        ResponseEntity<DadosDetalhamentoCardapioItemDTO> dadosCardapioItemDTOAtualizadoResponse = restClient.patch()
                .uri("/restaurantes/{restauranteId}/cardapio/{cardapioItemId}", restaurante.getId(), cardapioItem.getId())
                .body(dadosAtualizacaoCardapioItemDTO)
                .retrieve()
                .toEntity(DadosDetalhamentoCardapioItemDTO.class);

        //Then
        assertThat(dadosCardapioItemDTOAtualizadoResponse.getStatusCode().value()).isEqualTo(200);

        assertThat(dadosAtualizacaoCardapioItemDTO.nome()).isEqualTo(dadosCardapioItemDTOAtualizadoResponse.getBody().nome());
        assertThat(dadosAtualizacaoCardapioItemDTO.descricao()).isEqualTo(dadosCardapioItemDTOAtualizadoResponse.getBody().descricao());
        assertThat(dadosAtualizacaoCardapioItemDTO.fotoUrl()).isEqualTo(dadosCardapioItemDTOAtualizadoResponse.getBody().fotoUrl());
        assertThat(dadosCardapioItemDTOAtualizadoResponse.getBody().categoria()).isEqualTo(cardapioItem.getCategoria());
        assertThat(dadosCardapioItemDTOAtualizadoResponse.getBody().preco()).isEqualTo(cardapioItem.getPreco());
    }

    @Test
    @DisplayName("Deve inativar item do cardápio quando id Vávido")
    void deveInativarItemDoCardapio_QuandoIdValido() {
        //Given
        CardapioItem cardapioItem = criaCardapioItemMock();
        cardapioItem.setRestaurante(restaurante);
        cardapioRepository.save(cardapioItem);

        //When
        ResponseEntity<Void> response = restClient.delete()
                .uri("/restaurantes/{restauranteId}/cardapio/{cardapioItemId}", restaurante.getId(), cardapioItem.getId())
                .retrieve()
                .toBodilessEntity();

        //Then
        assertThat(response.getStatusCode().value()).isEqualTo(204);
        Optional<CardapioItem> itemInativo = cardapioRepository.findByIdAndRestauranteId(cardapioItem.getId(), restaurante.getId());
        assertThat(itemInativo.orElseThrow().getAtivo()).isFalse();
    }

    private static CardapioItem criaCardapioItemMock() {
        CardapioItem cardapioItem = new CardapioItem();
        cardapioItem.setNome("Escondidinho de carne seca");
        cardapioItem.setCategoria(CategoriaItem.PRATO_INDIVIDUAL);
        cardapioItem.setDescricao("Delicioso prato acompanhado de purê de abobóra");
        cardapioItem.setPreco(new BigDecimal("59.90"));
        cardapioItem.setAtivo(true);
        cardapioItem.setFotoUrl(null);
        return cardapioItem;
    }

    private static DadosCardapioItemDTO criaCardapioDTO() {
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

    private static List<CardapioItem> criaListaDeItensMock() {

        CardapioItem cardapioItem1 = new CardapioItem();
        cardapioItem1.setNome("Escondidinho de carne seca");
        cardapioItem1.setCategoria(CategoriaItem.PRATO_INDIVIDUAL);
        cardapioItem1.setDescricao("Delicioso prato acompanhado de purê de abobóra");
        cardapioItem1.setPreco(new BigDecimal("59.90"));
        cardapioItem1.setAtivo(false);
        cardapioItem1.setFotoUrl(null);

        CardapioItem cardapioItem2 = new CardapioItem();
        cardapioItem2.setNome("Picanha para dois");
        cardapioItem2.setCategoria(CategoriaItem.PRATO_PARA_DOIS);
        cardapioItem2.setDescricao("Picanha na brasa com acompanhamentos à escolha");
        cardapioItem2.setPreco(new BigDecimal("150.00"));
        cardapioItem2.setAtivo(true);
        cardapioItem2.setFotoUrl(null);

        return List.of(cardapioItem1, cardapioItem2);
    }
}
