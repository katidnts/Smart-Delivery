package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosEndereco;

public record DadosListaRestauranteDTO(
        Long id,
        String nome,
        String telefone,
        DadosEndereco endereco
) {
   public DadosListaRestauranteDTO(Restaurante restaurante) {
       this(
               restaurante.getId(),
               restaurante.getNome(),
               restaurante.getTelefone(),
               DadosEndereco.fromEntity(restaurante.getEndereco()));
   }
}
