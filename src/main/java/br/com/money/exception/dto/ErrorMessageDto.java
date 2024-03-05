package br.com.money.exception.dto;

import org.springframework.http.HttpStatus;

public record ErrorMessageDto(HttpStatus httpStatus, String message) {
}
