package br.com.money.model.dto;

import br.com.money.model.Activity;
import br.com.money.model.TypeAct;

import java.time.LocalDate;
import java.util.Date;

public record ActivityRequestDto(LocalDate date, String description, Double value, TypeAct type) {
}
