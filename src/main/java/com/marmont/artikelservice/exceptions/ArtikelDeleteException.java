package com.marmont.artikelservice.exceptions;

public class ArtikelDeleteException extends RuntimeException {
    public ArtikelDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}