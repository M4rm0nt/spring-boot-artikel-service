package com.marmont.artikelservice.exceptions;

public class ArtikelUpdateException extends RuntimeException {
    public ArtikelUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}