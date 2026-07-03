package br.com.katidantas.smartdelivery.endereco;

public record DadosDetalhamentoEnderecoDTO(

        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String localidade,
        String uf

) {
    public static DadosDetalhamentoEnderecoDTO fromEntity(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        DadosDetalhamentoEnderecoDTO dadosEndereco = new DadosDetalhamentoEnderecoDTO(
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getUf());

        return dadosEndereco;
    }

}
