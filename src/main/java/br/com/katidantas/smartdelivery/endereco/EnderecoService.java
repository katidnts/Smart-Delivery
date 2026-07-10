package br.com.katidantas.smartdelivery.endereco;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class EnderecoService {

    private final CepService cepService;

    public Endereco montaEnderecoCompleto(Endereco endereco) {
        EnderecoParcialDTO enderecoParcialDTO = cepService.buscarCep(endereco.getCep());

        endereco.setLogradouro(enderecoParcialDTO.logradouro());
        endereco.setBairro(enderecoParcialDTO.bairro());
        endereco.setCidade(enderecoParcialDTO.localidade());
        endereco.setUf(enderecoParcialDTO.uf());

        return endereco;
    }
}
