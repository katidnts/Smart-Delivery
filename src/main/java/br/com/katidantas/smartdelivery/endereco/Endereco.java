package br.com.katidantas.smartdelivery.endereco;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "endereco")
@Table(name = "enderecos")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"cep", "logradouro", "numero", "complemento", "bairro", "cidade", "uf"})
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
}
