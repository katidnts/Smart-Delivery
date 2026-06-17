package br.com.katidantas.smartdelivery.endereco;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnderecoController.class)
public class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CepService cepService;

    @Test
    @DisplayName("Deve retornar um endereço quando cep for válido")
    void deveRetornarEndereco_QuandoCepValido() throws Exception {

        //Given
        String cep = "22220001";

        EnderecoParcialDTO enderecoParcialDTO = new EnderecoParcialDTO(
                "22220001",
                "Rua do Catete",
                "Catete",
                "Rio de Janeiro",
                "RJ"
        );

        when(cepService.buscarCep(cep)).thenReturn(enderecoParcialDTO);

        //When
        String responseBody = mockMvc.perform(get("/endereco/{cep}", cep))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        EnderecoParcialDTO endereco = objectMapper.readValue(responseBody, EnderecoParcialDTO.class);

        //Then
        assertThat(endereco.cep()).isEqualTo(enderecoParcialDTO.cep());
        assertThat(endereco.logradouro()).isEqualTo(enderecoParcialDTO.logradouro());
        assertThat(endereco.bairro()).isEqualTo(enderecoParcialDTO.bairro());
        assertThat(endereco.localidade()).isEqualTo(enderecoParcialDTO.localidade());
        assertThat(endereco.uf()).isEqualTo(enderecoParcialDTO.uf());

        verify(cepService).buscarCep(cep);
    }

    @Test
    @DisplayName("Deve retornar 400 quando o CEP não existir ou for inválido")
    void deveRetornar400_QuandoCepNaoForEncontrado() throws Exception {

        //Given
        String cep = "99999999";

        when(cepService.buscarCep(cep)).thenThrow(new CepInvalidoException("CEP inválido!"));

        //When
        String responseBody = mockMvc.perform(get("/endereco/{cep}", cep))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //Then
        assertThat(responseBody).isEqualTo("CEP inválido!");
        verify(cepService).buscarCep(cep);
    }

}
