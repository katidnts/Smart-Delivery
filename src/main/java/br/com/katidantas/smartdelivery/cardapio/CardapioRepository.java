package br.com.katidantas.smartdelivery.cardapio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardapioRepository extends JpaRepository<CardapioItem, Long> {

    Optional<CardapioItem> findByIdAndRestauranteId(Long id, Long IdRestaurante);

    Page<CardapioItem> findAllByRestauranteId(Long idRestaurante, Pageable paginacao);

    Optional<CardapioItem> findByIdAndAtivo(Long itemId, boolean ativo);
}
