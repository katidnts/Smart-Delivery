package br.com.katidantas.smartdelivery.cardapio;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DadosCardapioItemDTO(
        @NotBlank
        String nome,
        String descricao,
        @NotNull
        CategoriaItem categoria,
        @NotNull
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
        BigDecimal preco,
        String fotoUrl
) {

    public CardapioItem toEntity() {
        CardapioItem cardapioItem = new CardapioItem();
        cardapioItem.setNome(this.nome);
        cardapioItem.setCategoria(this.categoria);
        cardapioItem.setDescricao(this.descricao);
        cardapioItem.setPreco(this.preco);
        cardapioItem.setFotoUrl(this.fotoUrl);

        return cardapioItem;
    }
}
