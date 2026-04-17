package br.com.katidantas.smartdelivery.endereco;

public record DadosEnderecoResponseDTO(

        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String localidade,
        String uf

) {
    public static DadosEnderecoResponseDTO fromEntity(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        DadosEnderecoResponseDTO dadosEndereco = new DadosEnderecoResponseDTO(
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
