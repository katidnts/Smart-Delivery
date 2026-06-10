package br.com.katidantas.smartdelivery.cardapio;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DadosDetalhamentoCardapioItemDTO(
        Long id,
        String nome,
        String descricao,
        CategoriaItem categoria,
        BigDecimal preco,
        String fotoUrl,
        Boolean ativo) {

    public DadosDetalhamentoCardapioItemDTO(CardapioItem cardapioItem) {
        this (
                cardapioItem.getId(),
                cardapioItem.getNome(),
                cardapioItem.getDescricao(),
                cardapioItem.getCategoria(),
                cardapioItem.getPreco(),
                cardapioItem.getFotoUrl(),
                cardapioItem.getAtivo()
        );
    }
}
