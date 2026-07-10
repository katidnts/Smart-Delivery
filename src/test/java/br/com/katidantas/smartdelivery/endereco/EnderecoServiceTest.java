package br.com.katidantas.smartdelivery.endereco;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EnderecoServiceTest {

    @Mock
    private CepService cepService;

    @InjectMocks
    private EnderecoService enderecoService;

    @Test
    public void deveMontarUmEnderecoCompleto_QuandoDadosValidos() {
        //Given
        EnderecoParcialDTO enderecoParcialDTO = new EnderecoParcialDTO(
                "22220001",
                "Rua do Catete",
                "Catete",
                "Rio de Janeiro",
                "RJ"
        );

        DadosEnderecoDTO enderecoRequest = new DadosEnderecoDTO(
                "22220001",
                "123",
                "500");

        when(cepService.buscarCep(enderecoRequest.cep())).thenReturn(enderecoParcialDTO);

        //When
        var enderecoCompleto = enderecoService.montaEnderecoCompleto(enderecoRequest.toEntity());

        //Then
        assertThat(enderecoCompleto.getCep()).isEqualTo(enderecoParcialDTO.cep());
        assertThat(enderecoCompleto.getLogradouro()).isEqualTo(enderecoParcialDTO.logradouro());
        assertThat(enderecoCompleto.getBairro()).isEqualTo(enderecoParcialDTO.bairro());
        assertThat(enderecoCompleto.getCidade()).isEqualTo(enderecoParcialDTO.localidade());
        assertThat(enderecoCompleto.getUf()).isEqualTo(enderecoParcialDTO.uf());
        assertThat(enderecoCompleto.getNumero()).isEqualTo(enderecoRequest.numero());
        assertThat(enderecoCompleto.getComplemento()).isEqualTo(enderecoRequest.complemento());

        verify(cepService).buscarCep(enderecoRequest.cep());
    }
}
