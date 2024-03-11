package br.com.money.model.dto;

import java.time.LocalDate;
import java.util.Date;

public record BetweenTwoDatesDto(LocalDate initialDate, LocalDate finalDate) {
}
