package br.com.katidantas.smartdelivery.cliente;

import br.com.katidantas.smartdelivery.endereco.DadosEnderecoDTO;

public record DadosAtualizacaoClienteDTO(
        String nome,
        String sobrenome,
        String telefone,
        DadosEnderecoDTO endereco
) {
    public Cliente toEntity() {
        Cliente cliente = new Cliente();
        cliente.setNome(this.nome);
        cliente.setSobrenome(this.sobrenome);
        cliente.setTelefone(this.telefone);
        if (this.endereco() != null) {
            cliente.setEndereco(this.endereco().toEntity());
        }
        return cliente;
    }
}
