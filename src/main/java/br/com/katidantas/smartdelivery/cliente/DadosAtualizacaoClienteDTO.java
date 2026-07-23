package br.com.katidantas.smartdelivery.cliente;

import br.com.katidantas.smartdelivery.endereco.DadosEnderecoDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record DadosAtualizacaoClienteDTO(
        String nome,
        String sobrenome,
        @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos numéricos")
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
