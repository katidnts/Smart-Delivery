package br.com.katidantas.smartdelivery.restaurante;

import java.util.List;

public record PageResponse<T>(List<T> content, int totalElements, int totalPages, int number) {
}
