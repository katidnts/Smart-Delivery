package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosEndereco;

public record DadosDetalhamentoRestaurante(
        Long id,
        String nome,
        String telefone,
        DadosEndereco endereco
) {
    public DadosDetalhamentoRestaurante(Restaurante restaurante) {
        this(
                restaurante.getId(),
                restaurante.getNome(),
                restaurante.getTelefone(),
                DadosEndereco.fromEntity(restaurante.getEndereco())
        );
    }
}
