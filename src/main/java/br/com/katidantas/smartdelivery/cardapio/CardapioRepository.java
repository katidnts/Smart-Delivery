package br.com.katidantas.smartdelivery.cardapio;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CardapioRepository extends JpaRepository<CardapioItem, Long> {
}
