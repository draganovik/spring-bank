package com.draganovik.currencyexchange.exceptions;

public class ExtendedExceptions {

    public static class NotFoundException extends Exception {
        public NotFoundException(String message) {
            super(message);
        }
    }
}
