package br.com.katidantas.smartdelivery.cardapio;

import br.com.katidantas.smartdelivery.restaurante.Restaurante;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cardapios")
public class CardapioItem {

    @Id
    @GeneratedValue
    private Long id;

    private CategoriaItem categoria;

    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int quantidade;
    private boolean ativo;

    @Column(name = "foto_url")
    private String fotoUrl;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;


}
