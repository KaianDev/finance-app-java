package br.com.money.exception;

public class ValidFieldsException extends RuntimeException {
    public ValidFieldsException(String msg) {
        super(msg);
    }
}
