package br.com.katidantas.smartdelivery.cardapio;

import br.com.katidantas.smartdelivery.restaurante.RestauranteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

}
