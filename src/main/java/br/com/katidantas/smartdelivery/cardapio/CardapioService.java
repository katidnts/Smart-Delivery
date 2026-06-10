package br.com.katidantas.smartdelivery.cardapio;

import br.com.katidantas.smartdelivery.restaurante.Restaurante;
import br.com.katidantas.smartdelivery.restaurante.RestauranteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CardapioService {

    private CardapioRepository cardapioRepository;
    private RestauranteRepository restauranteRepository;

    public CardapioService(CardapioRepository repository, RestauranteRepository restauranteRepository) {
        this.cardapioRepository = repository;
        this.restauranteRepository = restauranteRepository;
    }

    @Transactional
    public CardapioItem save(Long restauranteId, CardapioItem item) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId).orElseThrow(
                () -> new EntityNotFoundException("Restaurante não encontrado"));
        item.setRestaurante(restaurante);
        return cardapioRepository.save(item);
    }


    public CardapioItem buscarItemDoCardapio(Long idRestaurante, Long idItemCardapio) {

        return cardapioRepository
                .findByIdAndRestauranteId(idItemCardapio, idRestaurante).orElseThrow(() -> new EntityNotFoundException(
                        "Item com o id: %d não foi encontrado!".formatted(idItemCardapio)));
    }

    public Page<CardapioItem> buscarTodosOsItensDoCardapio(Long idRestaurante, Pageable paginacao) {
        return cardapioRepository.findAllByRestauranteId(idRestaurante, paginacao);

    }

    @Transactional
    public CardapioItem atualizarItemDoCardapio(Long restauranteId, Long itemId, CardapioItem cardapioItemAtualizado) {
        CardapioItem item = getItemDoCardapioByRestaurante(restauranteId, itemId);

        if (cardapioItemAtualizado.getNome() != null) {
            item.setNome(cardapioItemAtualizado.getNome());
        }
        if (cardapioItemAtualizado.getFotoUrl() != null) {
            item.setFotoUrl(cardapioItemAtualizado.getFotoUrl());
        }
        if (cardapioItemAtualizado.getCategoria() != null) {
            item.setCategoria(cardapioItemAtualizado.getCategoria());
        }
        if (cardapioItemAtualizado.getPreco() != null) {
            item.setPreco(cardapioItemAtualizado.getPreco());
        }
        if (cardapioItemAtualizado.getDescricao() != null) {
            item.setDescricao(cardapioItemAtualizado.getDescricao());
        }
        return item;
    }
    private CardapioItem getItemDoCardapioByRestaurante(Long restauranteId, Long itemId) {
        return cardapioRepository.findByIdAndRestauranteId(itemId, restauranteId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Item com o id: %d não foi encontrado!".formatted(itemId)));
    }
}


