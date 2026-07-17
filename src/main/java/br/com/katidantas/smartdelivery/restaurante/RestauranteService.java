package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.Endereco;
import br.com.katidantas.smartdelivery.endereco.EnderecoService;
import jakarta.persistence.EntityNotFoundException;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@AllArgsConstructor
@Service
public class RestauranteService {

    private final RestauranteRepository repository;

    private final EnderecoService enderecoService;


    @Transactional
    public Restaurante save(Restaurante restaurante) {

        if (repository.existsByCnpj(restaurante.getCnpj())) {
            throw new ValidacaoRestauranteException("CNPJ já cadastrado!");
        }
        if (repository.existsByTelefone(restaurante.getTelefone())) {
            throw new ValidacaoRestauranteException("Telefone já cadastrado!");
        }

        Endereco enderecoCompleto = enderecoService.montaEnderecoCompleto(restaurante.getEndereco());

        restaurante.setEndereco(enderecoCompleto);

        return repository.save(restaurante);
    }

    public Restaurante buscarRestaurantePorId(Long id) {

        return getRestauranteAtivo(id);
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
            Endereco enderecoCompleto = enderecoService.montaEnderecoCompleto(restauranteAtualizado.getEndereco());
            restaurante.setEndereco(enderecoCompleto);
        }
        return restaurante;
    }

    @Transactional
    public void inativar(Long id) {

        Restaurante restaurante = getRestauranteAtivo(id);
        restaurante.setAtivo(false);
    }

    // TODO: diferenciar mensagem de erro para id não encontrado e restaurante inativo
    private Restaurante getRestauranteAtivo(Long id) {
        return repository.findByIdAndAtivoEquals(id, true)
                .orElseThrow(() -> new EntityNotFoundException("O Restaurante com o id informado: %s não existe!".formatted(id)));
    }
}

