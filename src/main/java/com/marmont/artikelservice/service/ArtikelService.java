package com.marmont.artikelservice.service;

import com.marmont.artikelservice.dto.ArtikelResponse;
import com.marmont.artikelservice.dto.ArtikelRequest;
import com.marmont.artikelservice.exceptions.*;
import com.marmont.artikelservice.mapper.ArtikelMapper;
import com.marmont.artikelservice.model.Artikel;
import com.marmont.artikelservice.repository.ArtikelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtikelService {

    private final ArtikelRepository artikelRepository;
    private static final int DELETED = -1;
    private static final int INCREMENT_BY_ONE = 1;

    public ArtikelResponse createArtikel(ArtikelRequest artikelRequest) {
        Artikel artikel = ArtikelMapper.INSTANCE.mapToArtikel(artikelRequest);
        Artikel savedArtikel = artikelRepository.save(artikel);

        log.info("Artikel with id: {} is saved!", savedArtikel.getId());
        return ArtikelMapper.INSTANCE.mapToArtikelResponse(savedArtikel);
    }

    public List<ArtikelResponse> getAllArtikel() {
        List<Artikel> listOfArtikel = artikelRepository.findByStatusNot(DELETED);

        log.info("All Artikel found!");

        return listOfArtikel.stream()
                .map(ArtikelMapper.INSTANCE::mapToArtikelResponse)
                .toList();
    }

    public ArtikelResponse getArtikelById(long id) {
        Artikel artikel = artikelRepository.findById(id)
                .orElseThrow(() -> new ArtikelNotFoundException("Artikel not found with id: " + id));
        if (artikel.getStatus() == DELETED) {
            throw new ArtikelNotFoundException("Artikel not found with id: " + id);
        }

        log.info("Artikel found with id: {}", artikel.getId());

        return ArtikelMapper.INSTANCE.mapToArtikelResponse(artikel);
    }

    public ArtikelResponse updateArtikel(long id, ArtikelRequest artikelRequest) {
        Artikel existingArtikel = artikelRepository.findById(id)
                .orElseThrow(() -> new ArtikelNotFoundException("Artikel not found with id: " + id));

        existingArtikel.setEan(artikelRequest.getEan());
        existingArtikel.setBeschreibung(artikelRequest.getBeschreibung());
        existingArtikel.setEinkaufspreis(artikelRequest.getEinkaufspreis());
        existingArtikel.setVerkaufspreis(artikelRequest.getVerkaufspreis());

        existingArtikel.setVersion(existingArtikel.getVersion() + INCREMENT_BY_ONE);

        Artikel updatedArtikel = artikelRepository.save(existingArtikel);

        log.info("Artikel with id: {} is updated!", updatedArtikel.getId());

        return ArtikelMapper.INSTANCE.mapToArtikelResponse(updatedArtikel);
    }

    public void markAsDeleted(long id) {

        var artikel = artikelRepository.findById(id).orElseThrow(() -> new ArtikelNotFoundException("Artikel not found with id: " + id));
        artikel.setStatus(DELETED);
        artikelRepository.save(artikel);
        log.info("Artikel with id: {} is deleted!", id);
    }

    public List<ArtikelResponse> searchArtikel(String beschreibung, String ean) {

        if (beschreibung == null || ean == null) {
            return List.of();
        }

        var foundArtikels = artikelRepository.findByBeschreibungContainingOrEanContainingAndStatusNot(beschreibung, ean, DELETED);

        return foundArtikels.stream()
                .map(ArtikelMapper.INSTANCE::mapToArtikelResponse)
                .collect(Collectors.toList());
    }

}