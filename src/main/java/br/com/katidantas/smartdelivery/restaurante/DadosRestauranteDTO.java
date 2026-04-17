package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosEnderecoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record DadosRestauranteDTO(
        @NotBlank
        String nome,
        @NotBlank
        String telefone,
        @NotNull
        @Valid
        DadosEnderecoRequestDTO endereco
) {
    public Restaurante toEntity() {
        Restaurante restaurante = new Restaurante();
        restaurante.setAtivo(true);
        restaurante.setNome(this.nome);
        restaurante.setTelefone(this.telefone);
        restaurante.setEndereco(this.endereco.toEntity());
        return restaurante;
    }
}
