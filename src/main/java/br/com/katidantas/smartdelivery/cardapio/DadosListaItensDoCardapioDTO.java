package br.com.katidantas.smartdelivery.cardapio;

import java.math.BigDecimal;

public record DadosListaItensDoCardapioDTO(
        Long id,
        String nome,
        String descricao,
        CategoriaItem categoria,
        BigDecimal preco,
        String fotoUrl) {

    public DadosListaItensDoCardapioDTO(CardapioItem cardapioItem) {
        this(
                cardapioItem.getId(),
                cardapioItem.getNome(),
                cardapioItem.getDescricao(),
                cardapioItem.getCategoria(),
                cardapioItem.getPreco(),
                cardapioItem.getFotoUrl()
        );

    }
}
