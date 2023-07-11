package com.lianziyou.bot.base.exception;


@SuppressWarnings("all")
public class BussinessException extends RuntimeException {

    public BussinessException(String message) {
        super(message);
    }

    public void throwMessage() {
        throw this;
    }

}
