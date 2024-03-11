package br.com.money.exception;

public class DateNotFoundException extends RuntimeException{
    public DateNotFoundException(String msg) {
        super(msg);
    }
}
