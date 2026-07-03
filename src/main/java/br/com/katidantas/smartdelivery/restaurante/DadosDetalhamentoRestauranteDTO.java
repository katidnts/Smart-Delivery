package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosDetalhamentoEnderecoDTO;

public record DadosDetalhamentoRestauranteDTO(
        Long id,
        String nome,
        String telefone,
        String cnpj,
        DadosDetalhamentoEnderecoDTO endereco
) {
    public DadosDetalhamentoRestauranteDTO(Restaurante restaurante) {
        this(
                restaurante.getId(),
                restaurante.getNome(),
                restaurante.getTelefone(),
                restaurante.getCnpj(),
                DadosDetalhamentoEnderecoDTO.fromEntity(restaurante.getEndereco())
        );
    }
}
