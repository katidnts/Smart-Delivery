package br.com.katidantas.smartdelivery.endereco;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CepServiceTest {

    @InjectMocks
    private CepService cepService;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Test
    @DisplayName("Retorna um endereço quando cep enviado é valido")
    void deveRetornarUmEndereco_QuandoCepValido() {

        //Given

        CepResponseDTO cepResponseDTO = new CepResponseDTO(
                "22220001",
                "Rua do Catete",
                "Catete",
                "Rio de Janeiro",
                "RJ",
                null
        );

        EnderecoParcialDTO enderecoParcialDTO = new EnderecoParcialDTO(
                "22220001",
                "Rua do Catete",
                "Catete",
                "Rio de Janeiro",
                "RJ");

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CepResponseDTO.class)).thenReturn(cepResponseDTO);

        //When

        var resultado = cepService.buscarCep("22220001");

        //Then

        assertThat(resultado.cep()).isEqualTo(enderecoParcialDTO.cep());
        assertThat(resultado.logradouro()).isEqualTo(enderecoParcialDTO.logradouro());
        assertThat(resultado.bairro()).isEqualTo(enderecoParcialDTO.bairro());
        assertThat(resultado.localidade()).isEqualTo(enderecoParcialDTO.localidade());
        assertThat(resultado.uf()).isEqualTo(enderecoParcialDTO.uf());

    }

    @Test
    @DisplayName("Deve lançar CepInvalidoException quando o CEP for nulo ou com formato inválido")
    void deveLancarIllegalArgumentException_QuandoCepForNuloOuFormatoInvalido() {

        //Given

        String cep = "222200";

        //Then

        assertThatThrownBy(() -> cepService.buscarCep(cep))
                .isInstanceOf(CepInvalidoException.class)
                .hasMessage("CEP deve conter 8 dígitos");

    }

    @Test
    @DisplayName("Deve lançar CepInvalidoException quando o CEP não existir na API")
    void deveLancarIllegalArgumentException_QuandoCepNaoExistirNaApi() {

        //Given

        String cep = "99999999";

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CepResponseDTO.class)).thenReturn(new CepResponseDTO(cep, null, null, null, null, true));


        //Then

        assertThatThrownBy(() -> cepService.buscarCep(cep))
                .isInstanceOf(CepInvalidoException.class)
                .hasMessage("CEP inválido!");
    }


}
