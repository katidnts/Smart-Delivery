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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    @DisplayName("Deve retornar 404 quando cliente não encontrado")
    void deveRetornar404_QuandoClienteNaoEncontrado() {
        //When + Then
        assertThatThrownBy(() -> restClient.get()
                .uri("/clientes/{id}", 999999L)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(404);
                });
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
    @DisplayName("Deve retornar 400 quando CPF inválido")
    void deveRetornar400_QuandoCpfInvalido() {
        //Given
        DadosClienteDTO dadosClienteDTO = new DadosClienteDTO(
                "Joana",
                "Queiroz",
                "111.111.111-11",
                "21999999999",
                "joana.q@email.com",
                new DadosEnderecoDTO("22220001", "52", "902")
        );

        //When + Then
        assertThatThrownBy(() -> restClient.post()
                .uri("/clientes")
                .body(dadosClienteDTO)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(400);
                });
    }

    @Test
    @DisplayName("Deve retornar 400 quando CPF duplicado")
    void deveRetornar400_QuandoCpfDuplicado() {
        //Given
        Cliente clienteExistente = criaCliente();
        clienteRepository.save(clienteExistente);

        DadosClienteDTO dadosClienteDTO = new DadosClienteDTO(
                "Ana",
                "Silva",
                "52998224725", // mesmo CPF do clienteExistente
                "21988888888",
                "ana@email.com",
                new DadosEnderecoDTO("22220001", "52", "902")
        );

        //When + Then
        assertThatThrownBy(() -> restClient.post()
                .uri("/clientes")
                .body(dadosClienteDTO)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(400);
                });
    }

    @Test
    @DisplayName("Deve retornar 400 quando telefone inválido")
    void deveRetornar400_QuandoTelefoneInvalido() {
        //Given
        DadosClienteDTO dadosClienteDTO = new DadosClienteDTO(
                "Joana",
                "Queiroz",
                "52998224725",
                "123", // menos de 10 dígitos
                "joana.q@email.com",
                new DadosEnderecoDTO("22220001", "52", "902")
        );

        //When + Then
        assertThatThrownBy(() -> restClient.post()
                .uri("/clientes")
                .body(dadosClienteDTO)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(400);
                });
    }

    @Test
    @DisplayName("Deve retornar 400 quando telefone duplicado")
    void deveRetornar400_QuandoTelefoneDuplicado() {
        //Given
        Cliente clienteExistente = criaCliente();
        clienteRepository.save(clienteExistente);

        DadosClienteDTO dadosClienteDTO = new DadosClienteDTO(
                "Ana",
                "Silva",
                "11144477735", // CPF diferente
                "21999999999", // mesmo telefone do clienteExistente
                "ana@email.com", // email diferente
                new DadosEnderecoDTO("22220001", "52", "902")
        );

        //When + Then
        assertThatThrownBy(() -> restClient.post()
                .uri("/clientes")
                .body(dadosClienteDTO)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(400);
                });
    }

    @Test
    @DisplayName("Deve retornar 400 quando email inválido")
    void deveRetornar400_QuandoEmailInvalido() {
        //Given
        DadosClienteDTO dadosClienteDTO = new DadosClienteDTO(
                "Joana",
                "Queiroz",
                "52998224725",
                "21999999999",
                "email-sem-arroba.com", // sem @, formato inválido
                new DadosEnderecoDTO("22220001", "52", "902")
        );

        //When + Then
        assertThatThrownBy(() -> restClient.post()
                .uri("/clientes")
                .body(dadosClienteDTO)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(400);
                });
    }

    @Test
    @DisplayName("Deve retornar 400 quando email duplicado")
    void deveRetornar400_QuandoEmailDuplicado() {
        //Given
        Cliente clienteExistente = criaCliente();
        clienteRepository.save(clienteExistente);

        DadosClienteDTO dadosClienteDTO = new DadosClienteDTO(
                "Ana",
                "Silva",
                "11144477735", // CPF diferente
                "21988888888", // telefone diferente
                "joana.q@email.com", // mesmo email do clienteExistente
                new DadosEnderecoDTO("22220001", "52", "902")
        );

        //When + Then
        assertThatThrownBy(() -> restClient.post()
                .uri("/clientes")
                .body(dadosClienteDTO)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(400);
                });
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
    @DisplayName("Deve retornar lista vazia quando não existem clientes ativos")
    void deveRetornarListaVazia_QuandoNaoExistemClientesAtivos() {
        //When
        ResponseEntity<PageResponse<DadosListaClienteDTO>> response = restClient.get()
                .uri("/clientes")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<PageResponse<DadosListaClienteDTO>>() {
                });

        //Then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().content()).isEmpty();
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
    @DisplayName("Deve retornar 404 quando cliente não encontrado no PATCH")
    void deveRetornar404_QuandoClienteNaoEncontradoNoPatch() {
        //Given
        DadosAtualizacaoClienteDTO dadosAtualizacao = new DadosAtualizacaoClienteDTO(
                "Novo Nome", null, null, null, null
        );

        //When + Then
        assertThatThrownBy(() -> restClient.patch()
                .uri("/clientes/{id}", 999999L)
                .body(dadosAtualizacao)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(404);
                });
    }

    @Test
    @DisplayName("Deve retornar 404 quando cliente está inativo no PATCH")
    void deveRetornar404_QuandoClienteInativoNoPatch() {
        //Given
        Cliente clienteInativo = criaCliente();
        clienteInativo.setAtivo(false);
        clienteRepository.save(clienteInativo);

        DadosAtualizacaoClienteDTO dadosAtualizacao = new DadosAtualizacaoClienteDTO(
                "Novo Nome", null, null, null, null
        );

        //When + Then
        assertThatThrownBy(() -> restClient.patch()
                .uri("/clientes/{id}", clienteInativo.getId())
                .body(dadosAtualizacao)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(404);
                });
    }

    @Test
    @DisplayName("Deve retornar 400 quando telefone inválido no PATCH")
    void deveRetornar400_QuandoTelefoneInvalidoNoPatch() {
        //Given
        Cliente cliente = criaCliente();
        clienteRepository.save(cliente);

        DadosAtualizacaoClienteDTO dadosAtualizacao = new DadosAtualizacaoClienteDTO(
                null, null, "123", null, null
        );

        //When + Then
        assertThatThrownBy(() -> restClient.patch()
                .uri("/clientes/{id}", cliente.getId())
                .body(dadosAtualizacao)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(400);
                });
    }

    @Test
    @DisplayName("Deve retornar 400 quando telefone duplicado no PATCH")
    void deveRetornar400_QuandoTelefoneDuplicadoNoPatch() {
        //Given
        Cliente clienteExistente = criaCliente(); // telefone "21999999999"
        clienteRepository.save(clienteExistente);

        Cliente outroCliente = new Cliente();
        outroCliente.setNome("Ana");
        outroCliente.setSobrenome("Silva");
        outroCliente.setCpf("11144477735");
        outroCliente.setTelefone("21988888888");
        outroCliente.setEmail("ana@email.com");
        outroCliente.setAtivo(true);
        outroCliente.setEndereco(new Endereco(
                null, "22220001", "Rua do Catete", "52", "902",
                "Catete", "Rio de Janeiro", "RJ"
        ));
        clienteRepository.save(outroCliente);

        DadosAtualizacaoClienteDTO dadosAtualizacao = new DadosAtualizacaoClienteDTO(
                null, null, "21999999999", null, null // telefone do clienteExistente
        );

        //When + Then
        assertThatThrownBy(() -> restClient.patch()
                .uri("/clientes/{id}", outroCliente.getId())
                .body(dadosAtualizacao)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(400);
                });
    }

    @Test
    @DisplayName("Deve retornar 400 quando email inválido no PATCH")
    void deveRetornar400_QuandoEmailInvalidoNoPatch() {
        //Given
        Cliente cliente = criaCliente();
        clienteRepository.save(cliente);

        DadosAtualizacaoClienteDTO dadosAtualizacao = new DadosAtualizacaoClienteDTO(
                null, null, null, "email-invalido.com", null
        );

        //When + Then
        assertThatThrownBy(() -> restClient.patch()
                .uri("/clientes/{id}", cliente.getId())
                .body(dadosAtualizacao)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(400);
                });
    }

    @Test
    @DisplayName("Deve retornar 400 quando email duplicado no PATCH")
    void deveRetornar400_QuandoEmailDuplicadoNoPatch() {
        //Given
        Cliente clienteExistente = criaCliente(); // email "joana.q@email.com"
        clienteRepository.save(clienteExistente);

        Cliente outroCliente = new Cliente();
        outroCliente.setNome("Ana");
        outroCliente.setSobrenome("Silva");
        outroCliente.setCpf("11144477735");
        outroCliente.setTelefone("21988888888");
        outroCliente.setEmail("ana@email.com");
        outroCliente.setAtivo(true);
        outroCliente.setEndereco(new Endereco(
                null, "22220001", "Rua do Catete", "52", "902",
                "Catete", "Rio de Janeiro", "RJ"
        ));
        clienteRepository.save(outroCliente);

        DadosAtualizacaoClienteDTO dadosAtualizacao = new DadosAtualizacaoClienteDTO(
                null, null, null, "joana.q@email.com", null // email do clienteExistente
        );

        //When + Then
        assertThatThrownBy(() -> restClient.patch()
                .uri("/clientes/{id}", outroCliente.getId())
                .body(dadosAtualizacao)
                .retrieve()
                .toEntity(DadosDetalhamentoClienteDTO.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(400);
                });
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

    @Test
    @DisplayName("Deve retornar 404 quando cliente não encontrado no DELETE")
    void deveRetornar404_QuandoClienteNaoEncontradoNoDelete() {
        //When + Then
        assertThatThrownBy(() -> restClient.delete()
                .uri("/clientes/{id}", 999999L)
                .retrieve()
                .toBodilessEntity())
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(404);
                });
    }

    @Test
    @DisplayName("Deve retornar 404 quando cliente já está inativo no DELETE")
    void deveRetornar404_QuandoClienteJaInativoNoDelete() {
        //Given
        Cliente clienteInativo = criaCliente();
        clienteInativo.setAtivo(false);
        clienteRepository.save(clienteInativo);

        //When + Then
        assertThatThrownBy(() -> restClient.delete()
                .uri("/clientes/{id}", clienteInativo.getId())
                .retrieve()
                .toBodilessEntity())
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(ex -> {
                    HttpClientErrorException httpEx = (HttpClientErrorException) ex;
                    assertThat(httpEx.getStatusCode().value()).isEqualTo(404);
                });
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
        cliente.setNome("Joana");
        cliente.setSobrenome("Queiroz");
        cliente.setCpf("52998224725");
        cliente.setTelefone("21999999999");
        cliente.setEmail("joana.q@email.com");
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
                "Joana",
                "Queiroz",
                "52998224725",
                "21999999999",
                "joana.q@email.com",
                endereco
        );
    }

    private static List<Cliente> criaListaClientes() {
        Cliente cliente1 = new Cliente();
        cliente1.setNome("Joana");
        cliente1.setSobrenome("Queiroz");
        cliente1.setCpf("52998224725");
        cliente1.setTelefone("21999999999");
        cliente1.setEmail("joana.q@email.com");
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