package br.com.katidantas.smartdelivery.cardapio;

import br.com.katidantas.smartdelivery.restaurante.Restaurante;

import java.math.BigDecimal;

public record DadosCardapioItemDTO(String nome, String descricao, CategoriaItem categoria, BigDecimal preco, int quantidade, String fotoUrl, Boolean ativo) {
}
