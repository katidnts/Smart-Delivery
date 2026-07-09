package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosEnderecoDTO;
import jakarta.validation.Valid;

public record DadosAtualizacaoRestauranteDTO(
        String nome,
        String telefone,
        @Valid
        DadosEnderecoDTO endereco) {


    public Restaurante toEntity() {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(this.nome);
        restaurante.setTelefone(this.telefone);
        if (this.endereco() != null) {
            restaurante.setEndereco(this.endereco().toEntity());
        }
        return restaurante;
    }
}
