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
                .findByIdAndRestauranteId(idItemCardapio, idRestaurante);
    }

    public Page<CardapioItem> buscarTodosOsItensDoCardapio(Long idRestaurante, Pageable paginacao) {
        return cardapioRepository.findAllByRestauranteId(idRestaurante, paginacao);

    }
}


