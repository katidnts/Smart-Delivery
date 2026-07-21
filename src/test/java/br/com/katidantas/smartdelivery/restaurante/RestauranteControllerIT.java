package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosEnderecoDTO;
import br.com.katidantas.smartdelivery.endereco.Endereco;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RestauranteControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres =new PostgreSQLContainer<>("postgres:17");


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

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
    @DisplayName("Deve buscar restaurante quando id válido")
    void deveBuscarRestaurante_QuandoIdValido() {
        //Given
        Restaurante restaurante = criaRestaurante();

        restauranteRepository.save(restaurante);

        //When
        ResponseEntity<DadosDetalhamentoRestauranteDTO> dadosDetalhamentoRestauranteDTOResponse = restClient.get()
                .uri("/restaurantes/" + restaurante.getId())
                .retrieve()
                .toEntity(DadosDetalhamentoRestauranteDTO.class);

        DadosDetalhamentoRestauranteDTO dadosDetalhamentoRestauranteDTO = dadosDetalhamentoRestauranteDTOResponse.getBody();
        int statusCode = dadosDetalhamentoRestauranteDTOResponse.getStatusCode().value();

        //Then
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

    @Test
    @DisplayName("Deve criar restaurante quando dados válidos")
    void deveCriarRestaurante_QuandoDadosValidos() {
        //Given
        DadosRestauranteDTO restaurante = criaRestauranteDTO();

        //When
        ResponseEntity<DadosDetalhamentoRestauranteDTO> response = restClient.post()
                .uri("/restaurantes")
                .body(restaurante)
                .retrieve()
                .toEntity(DadosDetalhamentoRestauranteDTO.class);

        DadosDetalhamentoRestauranteDTO dadosDetalhamentoRestauranteDTO = response.getBody();
        int statusCode = response.getStatusCode().value();

        //Then
        assertThat(statusCode).isEqualTo(201);
        assertThat(dadosDetalhamentoRestauranteDTO).isNotNull();
        assertThat(dadosDetalhamentoRestauranteDTO.nome()).isEqualTo(restaurante.nome());
        assertThat(dadosDetalhamentoRestauranteDTO.telefone()).isEqualTo(restaurante.telefone());
        assertThat(dadosDetalhamentoRestauranteDTO.cnpj()).isEqualTo(restaurante.cnpj());
        assertThat(dadosDetalhamentoRestauranteDTO.endereco().cep()).isEqualTo(restaurante.endereco().cep());
        assertThat(dadosDetalhamentoRestauranteDTO.endereco().numero()).isEqualTo(restaurante.endereco().numero());
        assertThat(dadosDetalhamentoRestauranteDTO.endereco().complemento()).isEqualTo(restaurante.endereco().complemento());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando CNPJ duplicado")
    void deveLancarErro400_QuandoCnpjDuplicado() {

    }

    @Test
    @DisplayName("Deve criar um restaurante sem numero e complemento no endereço")
    void deveCriarUmRestauranteSemNumeroEComplemento() {
        //Given
        DadosRestauranteDTO restaurante = criaRestauranteDTO(null, null);

        //When
        ResponseEntity<DadosDetalhamentoRestauranteDTO> response = restClient.post()
                .uri("/restaurantes")
                .body(restaurante)
                .retrieve()
                .toEntity(DadosDetalhamentoRestauranteDTO.class);

        DadosDetalhamentoRestauranteDTO dadosDetalhamentoRestauranteDTO = response.getBody();
        int statusCode = response.getStatusCode().value();

        //Then
        assertThat(statusCode).isEqualTo(201);
        assertThat(dadosDetalhamentoRestauranteDTO).isNotNull();
        assertThat(dadosDetalhamentoRestauranteDTO.nome()).isEqualTo(restaurante.nome());
        assertThat(dadosDetalhamentoRestauranteDTO.telefone()).isEqualTo(restaurante.telefone());
        assertThat(dadosDetalhamentoRestauranteDTO.cnpj()).isEqualTo(restaurante.cnpj());
        assertThat(dadosDetalhamentoRestauranteDTO.endereco().cep()).isEqualTo(restaurante.endereco().cep());
        assertThat(dadosDetalhamentoRestauranteDTO.endereco().numero()).isNull();
        assertThat(dadosDetalhamentoRestauranteDTO.endereco().complemento()).isNull();
    }

    @Test
    @DisplayName("Deve buscar uma lista de restaurantes ativos")
    void deveBuscarUmaListaComRestaurantesAtivos() {
        //Given
        List<Restaurante> restaurantes = criaListaRestaurante();
        restauranteRepository.saveAll(restaurantes);

        //When
        ResponseEntity<PageResponse<DadosListaRestauranteDTO>> restaurantesAtivos = restClient.get()
                .uri("/restaurantes")
                .retrieve().toEntity(new ParameterizedTypeReference<PageResponse<DadosListaRestauranteDTO>>() {
                });

        List<DadosListaRestauranteDTO> responseDeRestaurantes = restaurantesAtivos.getBody().content();

        //Then
        assertThat(restaurantesAtivos.getStatusCode().value()).isEqualTo(200);
        assertThat(responseDeRestaurantes).hasSize(restaurantes.size());
        for (int i = 0; i < restaurantes.size(); i++) {
            assertThat(restaurantes.get(i).getNome()).isEqualTo(responseDeRestaurantes.get(i).nome());
        }
    }

    @Test
    @DisplayName("Deve atualizar restaurante quando dados válidos")
    void deveAtualizarRestaurante() {
        //Given
        Restaurante restaurante = criaRestaurante();
        restauranteRepository.save(restaurante);
        Long id = restaurante.getId();

        DadosAtualizacaoRestauranteDTO dadosRestaurante = new DadosAtualizacaoRestauranteDTO(
                "Novo nome",
                "0000000000",
                null
        );

        //When
        ResponseEntity<DadosDetalhamentoRestauranteDTO> restauranteAtualizado = restClient.patch()
                .uri("/restaurantes/{id}", id)
                .body(dadosRestaurante)
                .retrieve()
                .toEntity(DadosDetalhamentoRestauranteDTO.class);

        //Then
        assertThat(restauranteAtualizado.getStatusCode().value()).isEqualTo(200);

        assertThat(dadosRestaurante.nome()).isEqualTo(restauranteAtualizado.getBody().nome());

    }

    @Test
    @DisplayName("Deve inativar restaurante quando id válido")
    void deveInativarRestaurante() {
        //Given
        Restaurante restaurante = criaRestaurante();
        restauranteRepository.save(restaurante);
        Long id = restaurante.getId();

        //When
        ResponseEntity<Void> response = restClient.delete()
                .uri("/restaurantes/{id}", id)
                .retrieve()
                .toBodilessEntity();

        //Then
        assertThat(response.getStatusCode().value()).isEqualTo(204);
        Optional<Restaurante> restauranteInativo = restauranteRepository.findById(id);
        assertThat(restauranteInativo.orElseThrow().getAtivo()).isFalse();
    }

    private static Restaurante criaRestaurante() {
        Endereco endereco = new Endereco(
                null,
                "22220001",
                "Rua do Catete",
                "200", "315",
                "Catete",
                "Rio de Janeiro",
                "RJ");

        Restaurante restaurante =  new Restaurante();
        restaurante.setId(null);
        restaurante.setNome("Bar da onça");
        restaurante.setCnpj("11222333000181");
        restaurante.setTelefone("999999999");
        restaurante.setEndereco(endereco);
        restaurante.setAtivo(true);
        restaurante.setCardapio(null);

        return restaurante;

    }

    private static DadosRestauranteDTO criaRestauranteDTO() {
        return criaRestauranteDTO("52", "902");
    }

    private static DadosRestauranteDTO criaRestauranteDTO(String numero, String complemento) {
        DadosEnderecoDTO endereco = new DadosEnderecoDTO(
                "22220001",
                numero,
                complemento
        );

        DadosRestauranteDTO restaurante = new DadosRestauranteDTO(
                "Bar da onça",
                "999999999",
                "11222333000181",
                endereco
        );
        return restaurante;
    }

    private static List<Restaurante> criaListaRestaurante() {
        var restaurante1 = new Restaurante();
        restaurante1.setAtivo(true);
        restaurante1.setNome("Casa da feijoada");
        restaurante1.setCnpj("11222333000181");
        restaurante1.setTelefone("888888888");
        restaurante1.setEndereco(new Endereco(
                null,
                "22220001",
                "Rua do Catete",
                "52",
                "902",
                "Catete",
                "Rio de Janeiro",
                "RJ"
        ));

        var restaurante2 = new Restaurante();
        restaurante2.setAtivo(true);
        restaurante2.setNome("Casa do Sushi");
        restaurante2.setCnpj("22333444000181");
        restaurante2.setTelefone("222222222");
        restaurante2.setEndereco(new Endereco(
                null,
                "22220001",
                "Rua do Catete",
                "52",
                "902",
                "Catete",
                "Rio de Janeiro",
                "RJ"
        ));
        return List.of(restaurante1, restaurante2);
    }
}
