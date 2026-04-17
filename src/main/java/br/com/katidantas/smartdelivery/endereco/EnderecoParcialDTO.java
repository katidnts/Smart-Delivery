package br.com.katidantas.smartdelivery.endereco;

public record EnderecoParcialDTO(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf
) {
}
