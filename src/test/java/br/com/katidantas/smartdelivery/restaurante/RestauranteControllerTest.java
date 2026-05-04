package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosEnderecoRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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
                .andDo(print())
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
}
