package br.com.money.model.dto;

import br.com.money.model.Activity;
import br.com.money.model.TypeAct;

import java.time.LocalDate;
import java.util.Date;

public record ActivityResponseDto(Long id, LocalDate date, String description, Double value, TypeAct type) {
    public ActivityResponseDto(Activity activity) {
        this(activity.getId(), activity.getDate(), activity.getDescription(), activity.getValue(), activity.getType());
    }
}
