package br.com.money.exception;

public class ValueZeroException extends RuntimeException {
    public ValueZeroException(String msg) {
        super(msg);
    }
}
