package br.com.katidantas.smartdelivery.restaurante;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestauranteService {

    @Autowired
    private RestauranteRepository repository;

    @Transactional
    public Restaurante save(Restaurante restaurante) {
        return repository.save(restaurante);
    }

    public Optional<Restaurante> buscarRestaurantePorId(Long id) {
        Optional<Restaurante> restaurante = repository.findById(id);
        if (restaurante == null) {
            throw new IllegalArgumentException("ID não existe");
        }
        return restaurante;
    }

    @Transactional
    public Restaurante atualizarCampos(Long id, Restaurante restauranteAtualizado) {

        Restaurante restaurante = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID não encontrado"));

        if (restauranteAtualizado.getNome() != null) {
            restaurante.setNome(restauranteAtualizado.getNome());
        }
        if (restauranteAtualizado.getTelefone() != null) {
            restaurante.setTelefone(restauranteAtualizado.getTelefone());
        }
        if (restauranteAtualizado.getEndereco() != null) {
            restaurante.setEndereco(restauranteAtualizado.getEndereco());
        }
        return restaurante;
    }

    @Transactional
    public Restaurante inativar(Long id) {

        Restaurante restaurante = repository.findByIdAndIsAtivoEquals(id, true)
                .orElseThrow(() -> new EntityNotFoundException("O id informado: %s não existe!".formatted(id)));
        restaurante.setAtivo(false);
        return restaurante;


    }
}

