package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.Endereco;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;

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

    private String cnpj;

    @OneToOne(cascade= CascadeType.ALL)
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

    private Boolean ativo;

    public Boolean isAtivo() {
        return ativo;
    }
}
