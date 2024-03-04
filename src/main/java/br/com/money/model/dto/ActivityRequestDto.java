package br.com.money.model.dto;

import br.com.money.model.TypeAct;

import java.util.Date;

public record ActivityRequestDto(Date date, String description, Double value, TypeAct type) {
}
