package br.com.katidantas.smartdelivery.cliente;

import br.com.katidantas.smartdelivery.endereco.DadosEnderecoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

public record DadosClienteDTO(
        @NotBlank
        String nome,
        @NotBlank
        String sobrenome,
        @CPF
        @NotBlank
        String cpf,
        @NotBlank
        @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos numéricos")
        String telefone,
        @NotBlank
        @Email
        String email,
        @NotNull
        @Valid
        DadosEnderecoDTO endereco

) {
    public Cliente toEntity() {
        Cliente cliente = new Cliente();
        cliente.setNome(this.nome);
        cliente.setSobrenome(this.sobrenome);
        cliente.setCpf(this.cpf.replaceAll("\\D", ""));
        cliente.setTelefone(this.telefone.replaceAll("\\D", ""));
        cliente.setEmail(this.email);
        cliente.setEndereco(this.endereco.toEntity());
        return cliente;
    }
}
