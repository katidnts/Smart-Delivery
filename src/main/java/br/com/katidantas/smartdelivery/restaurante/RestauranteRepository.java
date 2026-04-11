package br.com.katidantas.smartdelivery.restaurante;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {


    Optional<Restaurante> findByIdAndIsAtivoEquals(Long id, Boolean estado);

}
