package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.cardapio.CardapioItem;
import br.com.katidantas.smartdelivery.endereco.Endereco;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;

import java.util.List;

@Entity
@Table(name = "restaurantes")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String telefone;

    @NotBlank
    @Column(nullable = false, unique = true)
    @CNPJ
    private String cnpj;


    @OneToOne(cascade = CascadeType.ALL,  orphanRemoval = true)
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CardapioItem> cardapio;
}
