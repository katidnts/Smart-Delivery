package br.com.katidantas.smartdelivery.cardapio;

import br.com.katidantas.smartdelivery.restaurante.Restaurante;
import br.com.katidantas.smartdelivery.restaurante.RestauranteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jdk.jfr.RecordingState;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CardapioService {

    private CardapioRepository repository;
    private RestauranteRepository restauranteRepository;

    public CardapioService(CardapioRepository repository, RestauranteRepository restauranteRepository) {
        this.repository = repository;
        this.restauranteRepository = restauranteRepository;
    }

    @Transactional
    public CardapioItem save(Long restauranteId, CardapioItem item) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId).orElseThrow(
                () -> new EntityNotFoundException("Restaurante não encontrado"));
        item.setRestaurante(restaurante);
        return repository.save(item);
    }
}


