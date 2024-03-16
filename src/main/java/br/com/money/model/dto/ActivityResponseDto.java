package br.com.money.model.dto;

import br.com.money.model.Activity;
import br.com.money.model.TypeAct;
import br.com.money.model.User;

import java.time.LocalDate;
import java.util.Date;

public record ActivityResponseDto(Long id, LocalDate date, String description, Double value, TypeAct type, Long userId) {
    public ActivityResponseDto(Activity activity) {
        this(activity.getId(), activity.getDate(), activity.getDescription(), activity.getValue(), activity.getType(), activity.getUser().getId());
    }
}
