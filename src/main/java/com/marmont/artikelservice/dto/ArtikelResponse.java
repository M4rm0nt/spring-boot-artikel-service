package com.marmont.artikelservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArtikelResponse {

    private long id;
    private String ean;
    private String beschreibung;
    private BigDecimal einkaufspreis;
    private BigDecimal verkaufspreis;
    private LocalDateTime cdate;
    private LocalDateTime mdate;
    private int version;
    private int status;
}
