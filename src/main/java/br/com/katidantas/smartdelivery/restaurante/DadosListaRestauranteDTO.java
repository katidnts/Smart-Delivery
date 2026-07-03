package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosDetalhamentoEnderecoDTO;

public record DadosListaRestauranteDTO(
        Long id,
        String nome,
        String telefone,
        DadosDetalhamentoEnderecoDTO endereco
) {
    public DadosListaRestauranteDTO(Restaurante restaurante) {
        this(
                restaurante.getId(),
                restaurante.getNome(),
                restaurante.getTelefone(),
                DadosDetalhamentoEnderecoDTO.fromEntity(restaurante.getEndereco()));
    }
}
