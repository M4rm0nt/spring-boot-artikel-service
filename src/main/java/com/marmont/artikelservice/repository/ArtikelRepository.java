package com.marmont.artikelservice.repository;

import com.marmont.artikelservice.model.Artikel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtikelRepository extends JpaRepository<Artikel, Long> {
    List<Artikel> findByStatusNot(int status);
    List<Artikel> findByBeschreibungContainingOrEanContainingAndStatusNot(String beschreibung, String ean, int status);
}
