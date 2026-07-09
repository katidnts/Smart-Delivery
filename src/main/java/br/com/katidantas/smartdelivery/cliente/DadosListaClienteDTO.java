package br.com.katidantas.smartdelivery.cliente;

import br.com.katidantas.smartdelivery.endereco.DadosDetalhamentoEnderecoDTO;

public record DadosListaClienteDTO(
        Long id,
        String nome,
        String telefone,
        DadosDetalhamentoEnderecoDTO endereco
) {
    public DadosListaClienteDTO(Cliente cliente) {
        this(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                DadosDetalhamentoEnderecoDTO.fromEntity(cliente.getEndereco()));

    }
}
