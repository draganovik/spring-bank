package com.draganovik.bankaccount.exceptions;

public class ExtendedExceptions {
    public static class ForbiddenException extends Exception {
        public ForbiddenException() {

            super("Forbidden request and/or action.");
        }

        public ForbiddenException(String message) {

            super(message);
        }
    }

    public static class UnauthorizedException extends Exception {
        public UnauthorizedException() {

            super("Request is unauthorized.");
        }

        public UnauthorizedException(String message) {

            super(message);
        }
    }

    public static class BadRequestException extends Exception {
        public BadRequestException(String message) {

            super(message);
        }
    }

    public static class NotFoundException extends Exception {
        public NotFoundException(String message) {
            super(message);
        }
    }
}
