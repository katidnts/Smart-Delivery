package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.CepService;
import br.com.katidantas.smartdelivery.endereco.DadosEnderecoRequestDTO;

import br.com.katidantas.smartdelivery.endereco.EnderecoParcialDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestauranteServiceTest {

    @Mock
    private RestauranteRepository repository;

    @Mock
    private CepService cepService;

    @InjectMocks
    private RestauranteService restauranteService;

    @Test
    @DisplayName("Deve salvar e retornar o restaurante quando todos os dados estiverem corretos")
    void deveSalvarRestaurante_QuandoTodosOsDadosEstiveremCorretos() {

        //Given
        DadosEnderecoRequestDTO enderecoRequestDTO = new DadosEnderecoRequestDTO(
                "22220001",
                "123",
                "500"
        );

        Restaurante restaurante = new Restaurante(1L, "Casa do drink", "999999999", "11999999999", enderecoRequestDTO.toEntity(), true);

        EnderecoParcialDTO enderecoParcialDTO = new EnderecoParcialDTO(
                "22220001",
                "Rua do Catete",
                "Catete",
                "Rio de Janeiro",
                "RJ"
        );

        when(cepService.buscarCep("22220001")).thenReturn(enderecoParcialDTO);
        when(repository.save(any())).thenReturn(restaurante);

        //When

        Restaurante resultado = restauranteService.save(restaurante);

        //Then

        assertThat(resultado.getEndereco().getLogradouro()).isEqualTo("Rua do Catete");
        assertThat(resultado.getEndereco().getBairro()).isEqualTo("Catete");
        assertThat(resultado.getEndereco().getCidade()).isEqualTo("Rio de Janeiro");
        assertThat(resultado.getEndereco().getUf()).isEqualTo("RJ");

        verify(repository).save(restaurante);

    }

    @Test
    @DisplayName("Deve buscar um restaurante por ID quando o ID for válido")
    void deveBuscarRestaurante_QuandoIdValido() {

        //Given

        DadosEnderecoRequestDTO enderecoRequestDTO = new DadosEnderecoRequestDTO(
                "22220001",
                "123",
                "500"
        );

        Restaurante restaurante = new Restaurante(1L, "Casa do drink", "999999999", "11999999999", enderecoRequestDTO.toEntity(), true);

        when(repository.findByIdAndAtivoEquals(1L, true)).thenReturn(Optional.of(restaurante));

        //When

        Restaurante resultado = restauranteService.buscarRestaurantePorId(1L);

        //Then

        assertThat(resultado.getId()).isEqualTo(restaurante.getId());
        assertThat(resultado.getNome()).isEqualTo(restaurante.getNome());
        assertThat(resultado.getTelefone()).isEqualTo(restaurante.getTelefone());
        assertThat(resultado.getCnpj()).isEqualTo(restaurante.getCnpj());
        assertThat(resultado.getEndereco().getCep()).isEqualTo(enderecoRequestDTO.cep());
        assertThat(resultado.getEndereco().getNumero()).isEqualTo(enderecoRequestDTO.numero());
        assertThat(resultado.getEndereco().getComplemento()).isEqualTo(enderecoRequestDTO.complemento());
        assertThat(resultado.getAtivo()).isEqualTo(restaurante.getAtivo());

        verify(repository).findByIdAndAtivoEquals(1L, true);
    }
}
