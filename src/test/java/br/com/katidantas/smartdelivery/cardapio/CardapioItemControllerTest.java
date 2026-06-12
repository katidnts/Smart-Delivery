package br.com.katidantas.smartdelivery.cardapio;

import br.com.katidantas.smartdelivery.restaurante.DadosListaRestauranteDTO;
import br.com.katidantas.smartdelivery.restaurante.Restaurante;
import br.com.katidantas.smartdelivery.restaurante.RestauranteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardapioController.class)
@ActiveProfiles("test")
public class CardapioItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CardapioService cardapioService;

    @MockitoBean
    private RestauranteService restauranteService;

    @Test
    @DisplayName("Deve cadastrar item do cardápio quando todos os dados forem válidos ")
    void deveCadastrarCardapioItem_QuandoDadosValidos() throws Exception {

        //Arrange
        CardapioItem cardapioItem = criaCardapioItemMock();
        DadosCardapioItemDTO itemDTO = criaItemDTO();
        Long restauranteId = 5L;
        when(cardapioService.save(eq(restauranteId), any(CardapioItem.class))).thenReturn(cardapioItem);

        //When
        String responseBody = mockMvc.perform(post("/restaurantes/5/cardapio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //Then
        ArgumentCaptor<CardapioItem> cardapioItemArgumentCaptor = ArgumentCaptor.forClass(CardapioItem.class);
        verify(cardapioService).save(eq(restauranteId), cardapioItemArgumentCaptor.capture());

        assertThat(cardapioItemArgumentCaptor.getValue().getNome()).isEqualTo(itemDTO.nome());
        assertThat(cardapioItemArgumentCaptor.getValue().getCategoria()).isEqualTo(itemDTO.categoria());
        assertThat(cardapioItemArgumentCaptor.getValue().getDescricao()).isEqualTo(itemDTO.descricao());
        assertThat(cardapioItemArgumentCaptor.getValue().getPreco()).isEqualTo(itemDTO.preco());
        assertThat(cardapioItemArgumentCaptor.getValue().getFotoUrl()).isEqualTo(itemDTO.fotoUrl());
        assertThat(cardapioItemArgumentCaptor.getValue().getAtivo()).isTrue();

        DadosDetalhamentoCardapioItemDTO dadosDetalhamentoCardapioItemDTO = objectMapper.readValue(responseBody, DadosDetalhamentoCardapioItemDTO.class);

        assertThat(dadosDetalhamentoCardapioItemDTO.id()).isEqualTo(cardapioItem.getId());
        assertThat(dadosDetalhamentoCardapioItemDTO.nome()).isEqualTo(cardapioItem.getNome());
        assertThat(dadosDetalhamentoCardapioItemDTO.categoria()).isEqualTo(cardapioItem.getCategoria());
        assertThat(dadosDetalhamentoCardapioItemDTO.descricao()).isEqualTo(cardapioItem.getDescricao());
        assertThat(dadosDetalhamentoCardapioItemDTO.preco()).isEqualTo(cardapioItem.getPreco());
        assertThat(dadosDetalhamentoCardapioItemDTO.fotoUrl()).isEqualTo(cardapioItem.getFotoUrl());
        assertThat(dadosDetalhamentoCardapioItemDTO.ativo()).isEqualTo(cardapioItem.getAtivo());

    }

    @Test
    @DisplayName("Deve buscar item do cardápio quando id válido")
    public void deveBuscarItemDoCardapio_QuandoIdValido() throws Exception {
        //Arrange
        CardapioItem cardapioItem = criaCardapioItemMock();
        Long restauranteId = 5L;
        when(cardapioService.buscarItemDoCardapio(eq(restauranteId), eq(cardapioItem.getId()))).thenReturn(cardapioItem);

        //When
        String responseBody = mockMvc.perform(get("/restaurantes/5/cardapio/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DadosDetalhamentoCardapioItemDTO dadosDetalhamentoCardapioItemDTO = objectMapper.readValue(responseBody, DadosDetalhamentoCardapioItemDTO.class);

        //Then
        assertThat(dadosDetalhamentoCardapioItemDTO.id()).isEqualTo(cardapioItem.getId());
        assertThat(dadosDetalhamentoCardapioItemDTO.nome()).isEqualTo(cardapioItem.getNome());
        assertThat(dadosDetalhamentoCardapioItemDTO.descricao()).isEqualTo(cardapioItem.getDescricao());
        assertThat(dadosDetalhamentoCardapioItemDTO.categoria()).isEqualTo(cardapioItem.getCategoria());
        assertThat(dadosDetalhamentoCardapioItemDTO.fotoUrl()).isEqualTo(cardapioItem.getFotoUrl());
        assertThat(dadosDetalhamentoCardapioItemDTO.ativo()).isEqualTo(cardapioItem.getAtivo());

        verify(cardapioService).buscarItemDoCardapio(eq(restauranteId), eq(cardapioItem.getId()));


    }

    @Test
    @DisplayName("Deve buscar todos os itens ativos do cardápio")
    public void deveBuscarTodosOsItensDoCardapio_QuandoAtivos() throws Exception {
        //Arrange
        List<CardapioItem> itens = criaListaDeItensMock();
        Page<CardapioItem> paginaDeItens = new PageImpl<>(itens);
        Long restauranteId = 5L;
        when(cardapioService.buscarTodosOsItensDoCardapio(eq(restauranteId), any(Pageable.class))).thenReturn(paginaDeItens);

        //When

        String responseBody = mockMvc.perform(get("/restaurantes/5/cardapio"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //Then
        verify(cardapioService).buscarTodosOsItensDoCardapio(eq(restauranteId), any(Pageable.class));

        CardapioItenPage dto = objectMapper.readValue(responseBody, CardapioItenPage.class);

        assertListaCorrespondeEntidades(dto.getContent(), itens);

    }

    private static DadosCardapioItemDTO criaItemDTO() {
        DadosCardapioItemDTO itemDTO = new DadosCardapioItemDTO(
                "Escondidinho de carne seca",
                "Delicioso prato acompanhado de purê de abobóra",
                CategoriaItem.PRATO_INDIVIDUAL,
                new BigDecimal("59.90"),
                null

        );
        return itemDTO;
    }

    private static CardapioItem criaCardapioItemMock() {
        CardapioItem cardapioItem = new CardapioItem();
        cardapioItem.setId(1L);
        cardapioItem.setNome("Escondidinho de carne seca");
        cardapioItem.setCategoria(CategoriaItem.PRATO_INDIVIDUAL);
        cardapioItem.setDescricao("Delicioso prato acompanhado de purê de abobóra");
        cardapioItem.setPreco(new BigDecimal("59.90"));
        cardapioItem.setAtivo(true);
        cardapioItem.setFotoUrl(null);
        return cardapioItem;
    }

    private static List<CardapioItem> criaListaDeItensMock () {
        CardapioItem cardapioItem1 = new CardapioItem();
        cardapioItem1.setId(1L);
        cardapioItem1.setNome("Escondidinho de carne seca");
        cardapioItem1.setCategoria(CategoriaItem.PRATO_INDIVIDUAL);
        cardapioItem1.setDescricao("Delicioso prato acompanhado de purê de abobóra");
        cardapioItem1.setPreco(new BigDecimal("59.90"));
        cardapioItem1.setAtivo(true);
        cardapioItem1.setFotoUrl(null);

        CardapioItem cardapioItem2 = new CardapioItem();
        cardapioItem2.setId(2L);
        cardapioItem2.setNome("Picanha para dois");
        cardapioItem2.setCategoria(CategoriaItem.PRATO_PARA_DOIS);
        cardapioItem2.setDescricao("Picanha na brasa com acompanhamentos à escolha");
        cardapioItem2.setPreco(new BigDecimal("150.00"));
        cardapioItem2.setAtivo(true);
        cardapioItem2.setFotoUrl(null);

        return List.of(cardapioItem1, cardapioItem2);

    }


    private void assertDtoListaCorrespondeEntidade(DadosListaItensDoCardapioDTO dto, CardapioItem cardapioItem) {
        assertThat(dto.nome()).isEqualTo(cardapioItem.getNome());
        assertThat(dto.categoria()).isEqualTo(cardapioItem.getCategoria());
        assertThat(dto.descricao()).isEqualTo(cardapioItem.getDescricao());
        assertThat(dto.preco()).isEqualTo(cardapioItem.getPreco());
        assertThat(dto.fotoUrl()).isEqualTo(cardapioItem.getFotoUrl());
    }
    private void assertListaCorrespondeEntidades(List<DadosListaItensDoCardapioDTO> dtos, List<CardapioItem> itens) {
        assertThat(dtos).hasSize(itens.size());
        for (int i = 0; i < dtos.size(); i++) {
            assertDtoListaCorrespondeEntidade(dtos.get(i), itens.get(i));
        }
    }

}
