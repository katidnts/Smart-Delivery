package br.com.katidantas.smartdelivery.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    public Cliente save(Cliente cliente) {
        return cliente;
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
