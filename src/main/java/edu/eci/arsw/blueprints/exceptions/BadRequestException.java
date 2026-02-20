package edu.eci.arsw.blueprints.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
