package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.Endereco;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RestauranteControllerIT {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @LocalServerPort
    private int port;
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.create("http://localhost:" + port);
    }

    @AfterEach
    void limparBanco() {
        restauranteRepository.deleteAll();
    }

    @Test
    void deveBuscarRestaurantePorId() {

        //Arrange
        Endereco endereco = new Endereco(
                null,
                "22220001",
                "Rua do Catete",
                "200", "315",
                "Catete",
                "Rio de Janeiro",
                "RJ");

        Restaurante restaurante = new Restaurante(
                null,
                "Bar da onça",
                "999999999",
                "11222333000181",
                endereco,
                true);

        restauranteRepository.save(restaurante);

        //Act
        ResponseEntity<DadosDetalhamentoRestauranteDTO> dadosDetalhamentoRestauranteDTOResponse = restClient.get()
                .uri("/restaurantes/" + restaurante.getId())
                .retrieve()
                .toEntity(DadosDetalhamentoRestauranteDTO.class);

        DadosDetalhamentoRestauranteDTO dadosDetalhamentoRestauranteDTO = dadosDetalhamentoRestauranteDTOResponse.getBody();
        int statusCode = dadosDetalhamentoRestauranteDTOResponse.getStatusCode().value();


        //Assert
        assertThat(statusCode).isEqualTo(200);
        assertThat(dadosDetalhamentoRestauranteDTO).isNotNull();
        assertThat(dadosDetalhamentoRestauranteDTO.id()).isEqualTo(restaurante.getId());
        assertThat(dadosDetalhamentoRestauranteDTO.nome()).isEqualTo(restaurante.getNome());
        assertThat(dadosDetalhamentoRestauranteDTO.telefone()).isEqualTo(restaurante.getTelefone());
        assertThat(dadosDetalhamentoRestauranteDTO.endereco().cep()).isEqualTo(restaurante.getEndereco().getCep());
        assertThat(dadosDetalhamentoRestauranteDTO.endereco().logradouro()).isEqualTo(restaurante.getEndereco().getLogradouro());
        assertThat(dadosDetalhamentoRestauranteDTO.endereco().numero()).isEqualTo(restaurante.getEndereco().getNumero());
        assertThat(dadosDetalhamentoRestauranteDTO.endereco().complemento()).isEqualTo(restaurante.getEndereco().getComplemento());
        assertThat(dadosDetalhamentoRestauranteDTO.endereco().bairro()).isEqualTo(restaurante.getEndereco().getBairro());
        assertThat(dadosDetalhamentoRestauranteDTO.endereco().localidade()).isEqualTo(restaurante.getEndereco().getCidade());
        assertThat(dadosDetalhamentoRestauranteDTO.endereco().uf()).isEqualTo(restaurante.getEndereco().getUf());

    }


}
