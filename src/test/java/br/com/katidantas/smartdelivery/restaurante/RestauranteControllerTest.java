package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.Endereco;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestauranteController.class)
@ActiveProfiles("test")
public class RestauranteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RestauranteService restauranteService;

    public Restaurante criaRestauranteMock() {

        Endereco endereco = new Endereco();
        endereco.setCep("22220001");
        endereco.setLogradouro("Rua do Catete");
        endereco.setNumero("52");
        endereco.setComplemento("902");
        endereco.setBairro("Catete");
        endereco.setCidade("Rio de janeiro");
        endereco.setUf("RJ");

        Restaurante restauranteMock = new Restaurante();
        restauranteMock.setId(1L);
        restauranteMock.setNome("The best coffee");
        restauranteMock.setTelefone("999999999");
        restauranteMock.setCnpj("11222333000181");
        restauranteMock.setEndereco(endereco);
        restauranteMock.setAtivo(true);

        return restauranteMock;
    }

    public DadosAtualizacaoRestauranteDTO criaDTOAtualizacao() {
        DadosAtualizacaoRestauranteDTO dtoValido = new DadosAtualizacaoRestauranteDTO(
                1L,
                "Café atualizado",
                "888888888",
                null
        );
        return dtoValido;
    }

    @Test
    void deveCadastrarRestaurante_quandoDadosValidos() throws Exception {

        Restaurante restauranteMock = criaRestauranteMock();

        when(restauranteService.save(any())).thenReturn(restauranteMock);

        String responseBody = mockMvc.perform(post("/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restauranteMock))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DadosDetalhamentoRestauranteDTO responseDTO = objectMapper.readValue(responseBody, DadosDetalhamentoRestauranteDTO.class);

        Endereco endereco = restauranteMock.getEndereco();

        assertThat(responseDTO.nome()).isEqualTo(restauranteMock.getNome());
        assertThat(responseDTO.telefone()).isEqualTo(restauranteMock.getTelefone());
        assertThat(responseDTO.cnpj()).isEqualTo(restauranteMock.getCnpj());
        assertThat(responseDTO.endereco().cep()).isEqualTo(endereco.getCep());
        assertThat(responseDTO.endereco().numero()).isEqualTo(endereco.getNumero());
        assertThat(responseDTO.endereco().complemento()).isEqualTo(endereco.getComplemento());
        assertThat(responseDTO.endereco().logradouro()).isEqualTo(endereco.getLogradouro());
        assertThat(responseDTO.endereco().bairro()).isEqualTo(endereco.getBairro());
        assertThat(responseDTO.endereco().localidade()).isEqualTo(endereco.getCidade());
        assertThat(responseDTO.endereco().uf()).isEqualTo(endereco.getUf());

    }


    @Test
    void deveBuscarRestaurante_QuandoIdValido() throws Exception {

        Restaurante restauranteMock = criaRestauranteMock();

        when(restauranteService.buscarRestaurantePorId(1L)).thenReturn(restauranteMock);

        String responseBody = mockMvc.perform(get("/restaurantes/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DadosDetalhamentoRestauranteDTO response = objectMapper.readValue(responseBody, DadosDetalhamentoRestauranteDTO.class);

        assertThat(response.id()).isEqualTo(restauranteMock.getId());
        assertThat(response.nome()).isEqualTo(restauranteMock.getNome());
        assertThat(response.telefone()).isEqualTo(restauranteMock.getTelefone());
        assertThat(response.cnpj()).isEqualTo(restauranteMock.getCnpj());
        assertThat(response.endereco().cep()).isEqualTo(restauranteMock.getEndereco().getCep());
        assertThat(response.endereco().logradouro()).isEqualTo(restauranteMock.getEndereco().getLogradouro());
        assertThat(response.endereco().numero()).isEqualTo(restauranteMock.getEndereco().getNumero());
        assertThat(response.endereco().complemento()).isEqualTo(restauranteMock.getEndereco().getComplemento());
        assertThat(response.endereco().bairro()).isEqualTo(restauranteMock.getEndereco().getBairro());
        assertThat(response.endereco().localidade()).isEqualTo(restauranteMock.getEndereco().getCidade());
        assertThat(response.endereco().uf()).isEqualTo(restauranteMock.getEndereco().getUf());

    }

    @Test
    void deveBuscarListaDeRestaurantesAtivos() throws Exception {
        var restaurante1 = new Restaurante();
        restaurante1.setAtivo(true);
        restaurante1.setNome("Casa da feijoada");
        restaurante1.setCnpj("34665790000109");
        restaurante1.setTelefone("888888888");
        restaurante1.setEndereco(new Endereco(
                1L,
                "22220001",
                "Rua do Catete",
                "52",
                "902",
                "Catete",
                "Rio de Janeiro",
                "Uf"
        ));

        var restaurante2 = new Restaurante();
        restaurante2.setAtivo(true);
        restaurante2.setNome("Casa do Sushi");
        restaurante2.setCnpj("07526557000100");
        restaurante2.setTelefone("222222222");
        restaurante2.setEndereco(new Endereco(
                1L,
                "22220001",
                "Rua do Catete",
                "52",
                "902",
                "Catete",
                "Rio de Janeiro",
                "Uf"
        ));

        Page<Restaurante> page = new PageImpl<>(List.of(restaurante1, restaurante2));

        when(restauranteService.listarRestaurantes(any(Pageable.class))).thenReturn(page);

        String responseBody = mockMvc.perform(get("/restaurantes"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(restauranteService).listarRestaurantes(any(Pageable.class));

        RestaurantePage response = objectMapper.readValue(responseBody, RestaurantePage.class);

        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().getFirst().nome()).isEqualTo("Casa da feijoada");
        assertThat(response.getContent().getLast().nome()).isEqualTo("Casa do Sushi");

    }

    @Test
    void deveAtualizarRestaurante() throws Exception {

        Restaurante restauranteMock = criaRestauranteMock();
        DadosAtualizacaoRestauranteDTO dtoValido = criaDTOAtualizacao();

        ArgumentCaptor<Restaurante> restauranteArgumentCaptor = ArgumentCaptor.forClass(Restaurante.class);

        Long id = 1L;
        when(restauranteService.atualizarCampos(eq(id), restauranteArgumentCaptor.capture())).thenReturn(restauranteMock);

        String responseBody = mockMvc.perform(patch("/restaurantes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValido))

                ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Restaurante restauranteArgumentCaptorValue = restauranteArgumentCaptor.getValue();


        DadosDetalhamentoRestauranteDTO response = objectMapper.readValue(responseBody, DadosDetalhamentoRestauranteDTO.class);


        assertThat(response.nome()).isEqualTo(restauranteMock.getNome());
        assertThat(response.telefone()).isEqualTo(restauranteMock.getTelefone());
        assertThat(response.cnpj()).isEqualTo(restauranteMock.getCnpj());
        assertThat(response.endereco().cep()).isEqualTo(restauranteMock.getEndereco().getCep());
        assertThat(response.endereco().logradouro()).isEqualTo(restauranteMock.getEndereco().getLogradouro());
        assertThat(response.endereco().numero()).isEqualTo(restauranteMock.getEndereco().getNumero());
        assertThat(response.endereco().complemento()).isEqualTo(restauranteMock.getEndereco().getComplemento());
        assertThat(response.endereco().bairro()).isEqualTo(restauranteMock.getEndereco().getBairro());
        assertThat(response.endereco().localidade()).isEqualTo(restauranteMock.getEndereco().getCidade());
        assertThat(response.endereco().uf()).isEqualTo(restauranteMock.getEndereco().getUf());

    }

    @Test
    void deveDeletarRestauranteQuandoIdValido() throws Exception {

        Restaurante restauranteMock = criaRestauranteMock();
        Long id = restauranteMock.getId();

        when(restauranteService.inativar(id)).thenReturn(restauranteMock);

        mockMvc.perform(delete("/restaurantes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        verify(restauranteService, times(1)).inativar(id);
    }
}



