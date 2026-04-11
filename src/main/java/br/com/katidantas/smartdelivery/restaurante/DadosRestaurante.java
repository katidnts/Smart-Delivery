package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosEndereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record DadosRestaurante(
        @NotBlank
        String nome,
        @NotBlank
        String telefone,
        @NotNull
        @Valid
        DadosEndereco endereco
) {
    public Restaurante toEntity() {
        Restaurante restaurante = new Restaurante();
        restaurante.setAtivo(true);
        restaurante.setNome(this.nome);
        restaurante.setTelefone(this.telefone);
        restaurante.setEndereco(endereco.toEntity());
        return restaurante;
    }
}
