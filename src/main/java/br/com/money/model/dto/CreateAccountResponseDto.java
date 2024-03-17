package br.com.money.model.dto;

import br.com.money.model.User;

public record CreateAccountResponseDto(String name, String email, Boolean status) {

    public CreateAccountResponseDto(User user) {
        this(user.getName(), user.getEmail(), user.getStatus());
    }
}
