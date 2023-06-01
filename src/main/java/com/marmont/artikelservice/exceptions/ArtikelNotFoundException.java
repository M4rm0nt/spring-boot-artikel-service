package com.marmont.artikelservice.exceptions;

public class ArtikelNotFoundException extends RuntimeException {
    public ArtikelNotFoundException(String message) {
        super(message, cause);
    }
}