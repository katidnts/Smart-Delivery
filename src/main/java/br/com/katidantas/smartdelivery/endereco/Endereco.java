package br.com.katidantas.smartdelivery.endereco;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "endereco")
@Table(name = "enderecos")
@Setter
@Getter
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
