package br.com.money.model.dto;

import br.com.money.model.User;

public record CreateAccountResponseDto(String name, String email) {

    public CreateAccountResponseDto(User user) {
        this(user.getName(), user.getEmail());
    }
}
