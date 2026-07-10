package br.com.katidantas.smartdelivery.endereco;

public record EnderecoParcialDTO(
        String cep,
        String logradouro,
        String bairro,
        String localidade,
        String uf
) {

    public Endereco toEntity() {
        Endereco endereco = new Endereco();
        endereco.setCep(this.cep);
        endereco.setLogradouro(this.logradouro);
        endereco.setBairro(this.bairro);
        endereco.setCidade(this.localidade);
        endereco.setUf(this.uf);
        return endereco;
    }
}
