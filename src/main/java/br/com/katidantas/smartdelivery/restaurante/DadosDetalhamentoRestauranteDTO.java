package br.com.katidantas.smartdelivery.restaurante;

import br.com.katidantas.smartdelivery.endereco.DadosEnderecoResponseDTO;

public record DadosDetalhamentoRestauranteDTO(
        Long id,
        String nome,
        String telefone,
        DadosEnderecoResponseDTO endereco
) {
    public DadosDetalhamentoRestauranteDTO(Restaurante restaurante) {
        this(
                restaurante.getId(),
                restaurante.getNome(),
                restaurante.getTelefone(),
                DadosEnderecoResponseDTO.fromEntity(restaurante.getEndereco())
        );
    }
}
