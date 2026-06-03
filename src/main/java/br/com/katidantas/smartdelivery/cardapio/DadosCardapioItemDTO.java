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
        String fotoUrl,
        @NotNull
        Boolean ativo) {
}
