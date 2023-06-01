package com.marmont.artikelservice.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class ArtikelRequest {

    private long id;
    private String ean;
    private String beschreibung;
    private BigDecimal einkaufspreis;
    private BigDecimal verkaufspreis;
}