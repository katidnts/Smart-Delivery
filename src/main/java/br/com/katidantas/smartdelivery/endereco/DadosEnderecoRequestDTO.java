package br.com.katidantas.smartdelivery.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DadosEnderecoRequestDTO(
        @NotBlank
        @Pattern(regexp = "\\d{8}")
        String cep,
        String numero,
        String complemento

) {
    public Endereco toEntity() {
        Endereco endereco = new Endereco();
        endereco.setCep(this.cep);
        endereco.setNumero(this.numero);
        endereco.setComplemento(this.complemento);
        return endereco;
    }
}
