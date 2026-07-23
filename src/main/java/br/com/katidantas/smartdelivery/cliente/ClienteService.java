package br.com.katidantas.smartdelivery.cliente;

import br.com.katidantas.smartdelivery.endereco.Endereco;

import br.com.katidantas.smartdelivery.endereco.EnderecoService;
import jakarta.persistence.EntityNotFoundException;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class ClienteService {

    private final ClienteRepository repository;

    private final EnderecoService enderecoService;

    @Transactional
    public Cliente save(Cliente cliente) {

        if (repository.existsByCpf(cliente.getCpf())) {
            throw new ValidacaoClienteException("CPF já cadastrado!");
        }
        if (repository.existsByTelefone(cliente.getTelefone())) {
            throw new ValidacaoClienteException("Telefone já cadastrado!");
        }
        if (repository.existsByEmail(cliente.getEmail())) {
            throw new ValidacaoClienteException("Email já cadastrado!");
        }

        Endereco endereco = enderecoService.montaEnderecoCompleto(cliente.getEndereco());
        cliente.setEndereco(endereco);

        return repository.save(cliente);
    }

    public Cliente buscarCliente(Long id) {
        return getClienteAtivo(id);
    }

    public Page<Cliente> listarClientes(Pageable paginacao) {
        return repository.findAllByAtivoTrue(paginacao);
    }

    @Transactional
    public Cliente atualizarCampos(Long id, Cliente clienteAtualizado) {
        Cliente cliente = getClienteAtivo(id);

        if (clienteAtualizado.getNome() != null) {
            cliente.setNome(clienteAtualizado.getNome());
        }
        if (clienteAtualizado.getSobrenome() != null) {
            cliente.setSobrenome(clienteAtualizado.getSobrenome());
        }
        if (clienteAtualizado.getTelefone() != null) {
            if (repository.existsByTelefoneAndIdNot(clienteAtualizado.getTelefone(), id)) {
                throw new ValidacaoClienteException("Telefone já cadastrado!");
            }
            cliente.setTelefone(clienteAtualizado.getTelefone());
        }
        if (clienteAtualizado.getEmail() != null) {
            if (repository.existsByEmailAndIdNot(clienteAtualizado.getEmail(), id)) {
                throw new ValidacaoClienteException("Email já cadastrado!");
            }
            cliente.setEmail(clienteAtualizado.getEmail());
        }
        if (clienteAtualizado.getEndereco() != null) {
            Endereco endereco = enderecoService.montaEnderecoCompleto(clienteAtualizado.getEndereco());
            cliente.setEndereco(endereco);
        }
        return cliente;
    }

    @Transactional
    public void inativar(Long id) {
        Cliente cliente = getClienteAtivo(id);
        cliente.setAtivo(false);
    }

    private Cliente getClienteAtivo(Long id) {
        return repository.findByIdAndAtivoEquals(id, true)
                .orElseThrow(() -> new EntityNotFoundException("O Cliente com o id informado: %s não existe!".formatted(id)));
    }
}
