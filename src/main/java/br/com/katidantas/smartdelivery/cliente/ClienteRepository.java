package br.com.katidantas.smartdelivery.cliente;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Boolean existsByCpf(String cpf);
}
