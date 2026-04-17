package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.CepService;
import br.com.katidantas.smartdelivery.endereco.Endereco;
import br.com.katidantas.smartdelivery.endereco.EnderecoParcialDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RestauranteService {

    private final RestauranteRepository repository;

    private final CepService cepService;


    @Transactional
    public Restaurante save(Restaurante restaurante) {
        String cep = restaurante.getEndereco().getCep();

        EnderecoParcialDTO enderecoParcial = cepService.buscarCep(cep);
        Endereco endereco = restaurante.getEndereco();

        endereco.setLogradouro(enderecoParcial.logradouro());
        endereco.setBairro(enderecoParcial.bairro());
        endereco.setCidade(enderecoParcial.localidade());
        endereco.setUf(enderecoParcial.uf());

        restaurante.setEndereco(endereco);

        return repository.save(restaurante);
    }

    @Transactional
    public Restaurante buscarRestaurantePorId(Long id) {

        Restaurante restaurante = getRestauranteAtivo(id);
        return restaurante;
    }

    public Page<Restaurante> listarRestaurantes(Pageable paginacao) {

        return repository.findAllByAtivoTrue(paginacao);
    }

    @Transactional
    public Restaurante atualizarCampos(Long id, Restaurante restauranteAtualizado) {

        Restaurante restaurante = getRestauranteAtivo(id);

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

        Restaurante restaurante = getRestauranteAtivo(id);
        restaurante.setAtivo(false);

        return repository.save(restaurante);
    }

    private Restaurante getRestauranteAtivo(Long id) {
        return repository.findByIdAndAtivoEquals(id, true)
                .orElseThrow(() -> new EntityNotFoundException("O Restaurante com o id informado: %s não existe!".formatted(id)));
    }
}

