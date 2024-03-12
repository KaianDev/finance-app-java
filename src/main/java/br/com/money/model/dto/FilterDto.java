package br.com.money.model.dto;

import java.time.LocalDate;

public record FilterDto(LocalDate oneDate, LocalDate secondDate, String typeValue) {
}
