package br.com.katidantas.smartdelivery.cliente;

import br.com.katidantas.smartdelivery.endereco.DadosEnderecoDTO;
import br.com.katidantas.smartdelivery.endereco.Endereco;
import br.com.katidantas.smartdelivery.restaurante.PageResponse;
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
public class ClienteControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ClienteRepository clienteRepository;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.create("http://localhost:" + port);
    }

    @AfterEach
    void limparBanco() {
        clienteRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve buscar cliente quando id válido")
    void deveBuscarCliente_QuandoIdValido() {
        //Given
        Cliente cliente = criaCliente();
        clienteRepository.save(cliente);

        //When
        ResponseEntity<DadosDetalhamentoClienteDTO> response = restClient.get()
                .uri("/clientes/{id}", cliente.getId())
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class);

        DadosDetalhamentoClienteDTO dto = response.getBody();

        //Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(cliente.getId());
        assertThat(dto.nome()).isEqualTo(cliente.getNome());
        assertThat(dto.sobrenome()).isEqualTo(cliente.getSobrenome());
        assertThat(dto.cpf()).isEqualTo(cliente.getCpf());
        assertThat(dto.telefone()).isEqualTo(cliente.getTelefone());
        assertThat(dto.email()).isEqualTo(cliente.getEmail());
        assertThat(dto.endereco().cep()).isEqualTo(cliente.getEndereco().getCep());
    }

    @Test
    @DisplayName("Deve criar cliente quando dados válidos")
    void deveCriarCliente_QuandoDadosValidos() {
        //Given
        DadosClienteDTO dadosClienteDTO = criaClienteDTO();

        //When
        ResponseEntity<DadosDetalhamentoClienteDTO> response = restClient.post()
                .uri("/clientes")
                .body(dadosClienteDTO)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class);

        DadosDetalhamentoClienteDTO dto = response.getBody();

        //Then
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(dto).isNotNull();
        assertThat(dto.nome()).isEqualTo(dadosClienteDTO.nome());
        assertThat(dto.sobrenome()).isEqualTo(dadosClienteDTO.sobrenome());
        assertThat(dto.cpf()).isEqualTo(dadosClienteDTO.cpf().replaceAll("\\D", ""));
        assertThat(dto.telefone()).isEqualTo(dadosClienteDTO.telefone());
        assertThat(dto.email()).isEqualTo(dadosClienteDTO.email());
        assertThat(dto.endereco().cep()).isEqualTo(dadosClienteDTO.endereco().cep());
    }

    @Test
    @DisplayName("Deve buscar lista de clientes ativos quando existem clientes ativos")
    void deveBuscarListaDeClientesAtivos_QuandoExistemClientesAtivos() {
        //Given
        List<Cliente> clientes = criaListaClientes();
        clienteRepository.saveAll(clientes);

        //When
        ResponseEntity<PageResponse<DadosListaClienteDTO>> response = restClient.get()
                .uri("/clientes")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<PageResponse<DadosListaClienteDTO>>() {
                });

        List<DadosListaClienteDTO> resultado = response.getBody().content();

        //Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(resultado).hasSize(clientes.size());
    }

    @Test
    @DisplayName("Deve atualizar cliente quando dados válidos")
    void deveAtualizarCliente_QuandoDadosValidos() {
        //Given
        Cliente cliente = criaCliente();
        clienteRepository.save(cliente);
        Long id = cliente.getId();

        DadosAtualizacaoClienteDTO dadosAtualizacao = new DadosAtualizacaoClienteDTO(
                "Novo Nome",
                null,
                null,
                null,
                null
        );

        //When
        ResponseEntity<DadosDetalhamentoClienteDTO> response = restClient.patch()
                .uri("/clientes/{id}", id)
                .body(dadosAtualizacao)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class);

        //Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().nome()).isEqualTo("Novo Nome");
    }

    @Test
    @DisplayName("Deve inativar cliente quando cliente ativo")
    void deveInativarCliente_QuandoClienteAtivo() {
        //Given
        Cliente cliente = criaCliente();
        clienteRepository.save(cliente);
        Long id = cliente.getId();

        //When
        ResponseEntity<Void> response = restClient.delete()
                .uri("/clientes/{id}", id)
                .retrieve()
                .toBodilessEntity();

        //Then
        assertThat(response.getStatusCode().value()).isEqualTo(204);
        Optional<Cliente> clienteInativo = clienteRepository.findById(id);
        assertThat(clienteInativo.orElseThrow().getAtivo()).isFalse();
    }

    private static Cliente criaCliente() {
        Endereco endereco = new Endereco(
                null,
                "22220001",
                "Rua do Catete",
                "200",
                "315",
                "Catete",
                "Rio de Janeiro",
                "RJ"
        );

        Cliente cliente = new Cliente();
        cliente.setNome("Katiuscia");
        cliente.setSobrenome("Dantas");
        cliente.setCpf("52998224725");
        cliente.setTelefone("21999999999");
        cliente.setEmail("katiuscia@email.com");
        cliente.setEndereco(endereco);
        cliente.setAtivo(true);

        return cliente;
    }

    private static DadosClienteDTO criaClienteDTO() {
        DadosEnderecoDTO endereco = new DadosEnderecoDTO(
                "22220001",
                "52",
                "902"
        );

        return new DadosClienteDTO(
                "Katiuscia",
                "Dantas",
                "52998224725",
                "21999999999",
                "katiuscia@email.com",
                endereco
        );
    }

    private static List<Cliente> criaListaClientes() {
        Cliente cliente1 = new Cliente();
        cliente1.setNome("Katiuscia");
        cliente1.setSobrenome("Dantas");
        cliente1.setCpf("52998224725");
        cliente1.setTelefone("21999999999");
        cliente1.setEmail("katiuscia@email.com");
        cliente1.setAtivo(true);
        cliente1.setEndereco(new Endereco(
                null, "22220001", "Rua do Catete", "52", "902",
                "Catete", "Rio de Janeiro", "RJ"
        ));

        Cliente cliente2 = new Cliente();
        cliente2.setNome("Ana");
        cliente2.setSobrenome("Silva");
        cliente2.setCpf("11144477735");
        cliente2.setTelefone("21988888888");
        cliente2.setEmail("ana@email.com");
        cliente2.setAtivo(true);
        cliente2.setEndereco(new Endereco(
                null, "22220001", "Rua do Catete", "52", "902",
                "Catete", "Rio de Janeiro", "RJ"
        ));

        return List.of(cliente1, cliente2);
    }
}