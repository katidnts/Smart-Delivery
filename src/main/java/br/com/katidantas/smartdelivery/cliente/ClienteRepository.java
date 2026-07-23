package br.com.katidantas.smartdelivery.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByCpf(String cpf);

    Page<Cliente> findAllByAtivoTrue(Pageable paginacao);

    Optional<Cliente> findByIdAndAtivoEquals(Long id, boolean ativo);

    boolean existsByTelefone(String telefone);

    boolean existsByEmail(String email);

    boolean existsByTelefoneAndIdNot(String telefone, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);
}

