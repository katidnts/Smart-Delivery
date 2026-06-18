package br.com.katidantas.smartdelivery.cardapio;

import br.com.katidantas.smartdelivery.endereco.Endereco;
import br.com.katidantas.smartdelivery.restaurante.Restaurante;
import br.com.katidantas.smartdelivery.restaurante.RestauranteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardapioServiceTest {

    @Mock
    private CardapioRepository cardapioRepository;

    @Mock
    private RestauranteRepository restauranteRepository;

    @InjectMocks
    private CardapioService cardapioService;

    @Test
    @DisplayName("Deve salvar item quando dados válidos")
    void deveSalvarCardapioItem_QuandoDadosValidos() {
        //Given
        Restaurante restaurante = criaRestauranteMock();
        CardapioItem cardapioItem = criaCardapioItemMock();

        when(cardapioRepository.save(any())).thenReturn(cardapioItem);
        when(restauranteRepository.findById(restaurante.getId())).thenReturn(Optional.of(restaurante));

        //When
        CardapioItem itemSalvo = cardapioService.save(restaurante.getId(), cardapioItem);

        //Then
        ArgumentCaptor<CardapioItem> argumentCaptoritemSalvo = ArgumentCaptor.forClass(CardapioItem.class);
        verify(cardapioRepository).save(argumentCaptoritemSalvo.capture());
        CardapioItem itemCapturado = argumentCaptoritemSalvo.getValue();

        assertThat(itemCapturado.getRestaurante()).isEqualTo(restaurante);
        assertThat(itemCapturado.getId()).isEqualTo(cardapioItem.getId());
        assertThat(itemCapturado.getNome()).isEqualTo(cardapioItem.getNome());
        assertThat(itemCapturado.getCategoria()).isEqualTo(cardapioItem.getCategoria());
        assertThat(itemCapturado.getDescricao()).isEqualTo(cardapioItem.getDescricao());
        assertThat(itemCapturado.getPreco()).isEqualTo(cardapioItem.getPreco());
        assertThat(itemCapturado.getAtivo()).isEqualTo(cardapioItem.getAtivo());

    }

    @Test
    @DisplayName("Deve lançar EntityNotFound quando id do restaurante não for encontrado")
    void deveLancarEntityNotFound_QuandoRestauranteNaoEncontrado() {
        //Given
        Long restauranteId = 5L;
        CardapioItem cardapioItem = criaCardapioItemMock();
        when(restauranteRepository.findById(eq(restauranteId))).thenReturn(Optional.empty());

        //When + Then
        assertThatThrownBy(() -> cardapioService.save(restauranteId, cardapioItem))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Restaurante não encontrado");
        verifyNoInteractions(cardapioRepository);
    }

    @Test
    @DisplayName("Deve buscar item do cardápio quando id válido")
    void deveBuscarItem_QuandoIdValido() {
        //Given
        Long restauranteId = 5L;
        CardapioItem cardapioItem = criaCardapioItemMock();

        when(cardapioRepository.findByIdAndRestauranteId(eq(cardapioItem.getId()), eq(restauranteId))).thenReturn(Optional.of(cardapioItem));

        //When

        CardapioItem itemEncontrado = cardapioService.buscarItemDoCardapio(restauranteId, cardapioItem.getId());

        //Then

        assertThat(itemEncontrado.getNome()).isEqualTo(cardapioItem.getNome());

        verify(cardapioRepository).findByIdAndRestauranteId(eq(cardapioItem.getId()), eq(restauranteId));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando id do item for inválido")
    void deveLancarEntityNotFoundException_QuandoItemIdInvalido() {
        //Given
        Long restauranteId = 5L;
        CardapioItem cardapioItem = criaCardapioItemMock();

        when(cardapioRepository.findByIdAndRestauranteId(eq(cardapioItem.getId()), eq(restauranteId))).thenReturn(Optional.empty());

        //
        assertThatThrownBy(() -> cardapioService.buscarItemDoCardapio(restauranteId, cardapioItem.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Item com o id: %d não foi encontrado!".formatted(cardapioItem.getId()));

        verify(cardapioRepository).findByIdAndRestauranteId(eq(cardapioItem.getId()), eq(restauranteId));
    }

    @Test
    @DisplayName("Deve retornar um page de itens ativos")
    void deveRetornarPageDeItens_QuandoAtivos() {
        //Given
        List<CardapioItem> listaItens = criaListaDeItensMock();
        Page<CardapioItem> pageItens = new PageImpl<>(listaItens);
        Long restauranteId = 5L;
        when(cardapioRepository.findAllByRestauranteIdAndAtivoTrue(eq(restauranteId), any(Pageable.class))).thenReturn(pageItens);

        //When
        Page<CardapioItem> cardapioItens = cardapioService.buscarTodosOsItensDoCardapio(restauranteId, pageItens.getPageable());

        //Then
        assertThat(cardapioItens.getContent()).containsExactlyElementsOf(listaItens);
        assertThat(cardapioItens.getTotalElements()).isEqualTo(listaItens.size());
        verify(cardapioRepository).findAllByRestauranteIdAndAtivoTrue(eq(restauranteId), any(Pageable.class));
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

    private Restaurante criaRestauranteMock() {

        Endereco endereco = new Endereco();
        endereco.setCep("22220001");
        endereco.setLogradouro("Rua do Catete");
        endereco.setNumero("52");
        endereco.setComplemento("902");
        endereco.setBairro("Catete");
        endereco.setCidade("Rio de janeiro");
        endereco.setUf("RJ");

        Restaurante entidade = new Restaurante();
        entidade.setId(5L);
        entidade.setNome("The new coffee");
        entidade.setTelefone("999998888");
        entidade.setCnpj("11222333000181");
        entidade.setEndereco(endereco);
        entidade.setAtivo(true);

        return entidade;

    }

    private static List<CardapioItem> criaListaDeItensMock() {
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
}
