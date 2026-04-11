package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosEndereco;
import jakarta.validation.Valid;

public record DadosAtualizacaoRestauranteDTO(
        Long id,
        String nome,
        String telefone,
        @Valid
        DadosEndereco endereco) {


    public Restaurante toEntity() {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(this.nome);
        restaurante.setTelefone(this.telefone);
        if(restaurante.getEndereco() != null ) {
            restaurante.setEndereco(this.endereco().toEntity());
        }
        return restaurante;
    }
}
