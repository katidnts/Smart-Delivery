package br.com.katidantas.smartdelivery.cliente;

import br.com.katidantas.smartdelivery.endereco.DadosDetalhamentoEnderecoDTO;

public record
DadosDetalhamentoClienteDTO(
        Long id,
        String nome,
        String sobrenome,
        String cpf,
        String telefone,
        DadosDetalhamentoEnderecoDTO endereco
) {
    public DadosDetalhamentoClienteDTO(Cliente cliente) {
        this(
                cliente.getId(),
                cliente.getNome(),
                cliente.getSobrenome(),
                cliente.getCpf(),
                cliente.getTelefone(),
                DadosDetalhamentoEnderecoDTO.fromEntity(cliente.getEndereco())
        );
    }
}
