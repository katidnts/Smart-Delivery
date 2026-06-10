package br.com.katidantas.smartdelivery.cardapio;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record DadosAtualizacaoCardapioItemDTO(
        String nome,
        String descricao,
        CategoriaItem categoria,
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
        BigDecimal preco,
        String fotoUrl
) {

    public CardapioItem toEntity() {
        CardapioItem cardapioItem = new CardapioItem();
        cardapioItem.setNome(this.nome);
        cardapioItem.setDescricao(this.descricao);
        cardapioItem.setCategoria(this.categoria);
        cardapioItem.setPreco(this.preco);
        cardapioItem.setFotoUrl(this.fotoUrl);
        return cardapioItem;
    }
}
