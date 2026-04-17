package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosEnderecoResponseDTO;

public record DadosListaRestauranteDTO(
        Long id,
        String nome,
        String telefone,
        DadosEnderecoResponseDTO endereco
) {
   public DadosListaRestauranteDTO(Restaurante restaurante) {
       this(
               restaurante.getId(),
               restaurante.getNome(),
               restaurante.getTelefone(),
               DadosEnderecoResponseDTO.fromEntity(restaurante.getEndereco()));
   }
}
