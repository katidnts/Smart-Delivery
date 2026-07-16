package br.com.katidantas.smartdelivery.cliente;

import br.com.katidantas.smartdelivery.endereco.DadosEnderecoDTO;
import jakarta.validation.constraints.Email;

public record DadosAtualizacaoClienteDTO(
        String nome,
        String sobrenome,
        String telefone,
        @Email
        String email,
        DadosEnderecoDTO endereco
) {
    public Cliente toEntity() {
        Cliente cliente = new Cliente();
        cliente.setNome(this.nome);
        cliente.setSobrenome(this.sobrenome);
        cliente.setTelefone(this.telefone);
        cliente.setEmail(this.email);
        if (this.endereco() != null) {
            cliente.setEndereco(this.endereco().toEntity());
        }
        return cliente;
    }
}
