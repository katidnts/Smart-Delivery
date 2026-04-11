package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.Endereco;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "restaurante")
@Table(name = "restaurantes")
@Setter
@Getter
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String telefone;

    @OneToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

    private Boolean ativo;

    public Boolean isAtivo() {
        return ativo;
    }
}
