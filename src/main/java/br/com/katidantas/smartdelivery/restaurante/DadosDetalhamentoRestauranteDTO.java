package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosEndereco;

public record DadosDetalhamentoRestauranteDTO(
        Long id,
        String nome,
        String telefone,
        DadosEndereco endereco
) {
    public DadosDetalhamentoRestauranteDTO(Restaurante restaurante) {
        this(
                restaurante.getId(),
                restaurante.getNome(),
                restaurante.getTelefone(),
                DadosEndereco.fromEntity(restaurante.getEndereco())
        );
    }
}
