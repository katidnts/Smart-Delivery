package br.com.katidantas.smartdelivery.endereco;

public record CepResponseDTO(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf,
        Boolean erro
) {
}
