package br.com.money.model.dto;

import br.com.money.model.User;

public record CreateAccountResponseDto(User user, String activationToken) {
}
