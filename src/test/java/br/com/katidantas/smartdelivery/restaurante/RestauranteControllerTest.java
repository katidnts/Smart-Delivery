package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosEnderecoRequestDTO;
import br.com.katidantas.smartdelivery.endereco.DadosEnderecoResponseDTO;
import br.com.katidantas.smartdelivery.endereco.Endereco;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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


    private DadosEnderecoRequestDTO dadosEnderecoRequestDTO() {
        return new DadosEnderecoRequestDTO("22220001",
                "52",
                "902"
        );
    }

    private DadosEnderecoResponseDTO dadosEnderecoResponseDTO() {
        return new DadosEnderecoResponseDTO(
                "22220001",
                "Rua do Catete",
                "52",
                "902",
                "Catete",
                "Rio de Janeiro",
                "RJ"
        );
    }


    @Test
    void deveCadastrarRestaurante_quandoDadosValidos() throws Exception {

        DadosRestauranteDTO dadosRestauranteDTO = new DadosRestauranteDTO(
                "Bar do Zezé",
                "999999999",
                "12345678000195",
                dadosEnderecoRequestDTO()
        );

        Restaurante restauranteMock = dadosRestauranteDTO.toEntity();
        restauranteMock.getEndereco().setLogradouro("Rua do Catete");
        restauranteMock.getEndereco().setBairro("Catete");
        restauranteMock.getEndereco().setCidade("Rio de Janeiro");
        restauranteMock.getEndereco().setUf("RJ");

        when(restauranteService.save(any())).thenReturn(restauranteMock);

        String responseBody = mockMvc.perform(post("/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dadosRestauranteDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DadosDetalhamentoRestauranteDTO responseDTO = objectMapper.readValue(responseBody, DadosDetalhamentoRestauranteDTO.class);

        assertThat(responseDTO.nome()).isEqualTo(dadosRestauranteDTO.nome());
        assertThat(responseDTO.telefone()).isEqualTo(dadosRestauranteDTO.telefone());
        assertThat(responseDTO.cnpj()).isEqualTo(dadosRestauranteDTO.cnpj());
        assertThat(responseDTO.endereco().cep()).isEqualTo(dadosRestauranteDTO.endereco().cep());
        assertThat(responseDTO.endereco().numero()).isEqualTo(dadosRestauranteDTO.endereco().numero());
        assertThat(responseDTO.endereco().complemento()).isEqualTo(dadosRestauranteDTO.endereco().complemento());
        assertThat(responseDTO.endereco().logradouro()).isEqualTo(restauranteMock.getEndereco().getLogradouro());
        assertThat(responseDTO.endereco().bairro()).isEqualTo(restauranteMock.getEndereco().getBairro());
        assertThat(responseDTO.endereco().localidade()).isEqualTo(restauranteMock.getEndereco().getCidade());
        assertThat(responseDTO.endereco().uf()).isEqualTo(restauranteMock.getEndereco().getUf());

    }


    @Test
    void deveBuscarRestaurante_QuandoIdValido() throws Exception {
        var restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Bar do Zezé");
        restaurante.setTelefone("999999999");
        restaurante.setCnpj("12345678000195");
        restaurante.setEndereco(new Endereco(
                1L,
                "22220001",
                "Rua do Catete",
                "52",
                "902",
                "Catete",
                "Rio de Janeiro",
                "Uf"
        ));
        restaurante.setAtivo(true);


        when(restauranteService.buscarRestaurantePorId(1L)).thenReturn(restaurante);

        String responseBody = mockMvc.perform(get("/restaurantes/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DadosDetalhamentoRestauranteDTO response = objectMapper.readValue(responseBody, DadosDetalhamentoRestauranteDTO.class);

        assertThat(response.id()).isEqualTo(restaurante.getId());
        assertThat(response.nome()).isEqualTo(restaurante.getNome());
        assertThat(response.telefone()).isEqualTo(restaurante.getTelefone());
        assertThat(response.cnpj()).isEqualTo(restaurante.getCnpj());
        assertThat(response.endereco().cep()).isEqualTo(restaurante.getEndereco().getCep());
        assertThat(response.endereco().logradouro()).isEqualTo(restaurante.getEndereco().getLogradouro());
        assertThat(response.endereco().numero()).isEqualTo(restaurante.getEndereco().getNumero());
        assertThat(response.endereco().complemento()).isEqualTo(restaurante.getEndereco().getComplemento());
        assertThat(response.endereco().bairro()).isEqualTo(restaurante.getEndereco().getBairro());
        assertThat(response.endereco().localidade()).isEqualTo(restaurante.getEndereco().getCidade());
        assertThat(response.endereco().uf()).isEqualTo(restaurante.getEndereco().getUf());

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
}



