package com.marmont.artikelservice.exceptions;

public class ArtikelCreateException extends RuntimeException {
    public ArtikelCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}