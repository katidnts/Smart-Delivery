package br.com.katidantas.smartdelivery.cliente;

import br.com.katidantas.smartdelivery.endereco.DadosDetalhamentoEnderecoDTO;

public record DadosListaClienteDTO(
        Long id,
        String nome,
        String telefone,
        String email,
        DadosDetalhamentoEnderecoDTO endereco
) {
    public DadosListaClienteDTO(Cliente cliente) {
        this(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEmail(),
                DadosDetalhamentoEnderecoDTO.fromEntity(cliente.getEndereco()));

    }
}
