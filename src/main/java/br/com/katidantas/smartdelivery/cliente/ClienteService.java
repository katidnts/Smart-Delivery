package br.com.katidantas.smartdelivery.cliente;

import br.com.katidantas.smartdelivery.endereco.CepService;
import br.com.katidantas.smartdelivery.endereco.Endereco;
import br.com.katidantas.smartdelivery.endereco.EnderecoParcialDTO;
import br.com.katidantas.smartdelivery.endereco.EnderecoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ClienteService {

    private final ClienteRepository repository;

    private final EnderecoService enderecoService;

    public Cliente save(Cliente cliente) {

        Endereco endereco = enderecoService.montaEnderecoCompleto(cliente.getEndereco());

        cliente.setEndereco(endereco);

        return repository.save(cliente);
    }

    public Cliente buscarCliente(Long id) {
        throw new UnsupportedOperationException("Ainda não implementado");
    }

    public Page<DadosListaClienteDTO> listarClientes(Pageable paginacao) {
        throw new UnsupportedOperationException("Ainda não implementado");
    }

    public Cliente atualizarCampos(Long id, Cliente cliente) {
        throw new UnsupportedOperationException("Ainda não implementado");
    }

    public void inativar(Long id) {
        throw new UnsupportedOperationException("Ainda não implementado");
    }
}
