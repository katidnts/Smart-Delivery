package br.com.katidantas.smartdelivery.endereco;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CepService {

    private final RestClient restClient;

    public CepService(RestClient restClient) {
        this.restClient = restClient;
    }

    public EnderecoParcialDTO buscarCep(String cep) {

        if (cep == null || !cep.matches("\\d{8}") ) {
            throw new IllegalArgumentException("CEP deve conter 8 dígitos");
        }

        CepResponseDTO response = restClient.get()
                .uri("/ws/{cep}/json/", cep)
                .retrieve()
                .body(CepResponseDTO.class);

        if (response.erro() != null && response.erro()) {
            throw new IllegalArgumentException("CEP inválido!");
        }

        return toEnderecoParcial(response);
    }

    private EnderecoParcialDTO toEnderecoParcial(CepResponseDTO response) {

        return new EnderecoParcialDTO(
                response.cep(),
                response.logradouro(),
                response.bairro(),
                response.localidade(),
                response.uf()
        );

    }
}
