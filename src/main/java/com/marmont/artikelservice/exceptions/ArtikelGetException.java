package com.marmont.artikelservice.exceptions;

public class ArtikelGetException extends RuntimeException {
    public ArtikelGetException(String message, Throwable cause) {
        super(message, cause);
    }
}