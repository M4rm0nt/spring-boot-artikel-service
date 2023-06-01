package com.marmont.artikelservice.controller;

import com.marmont.artikelservice.dto.ArtikelRequest;
import com.marmont.artikelservice.dto.ArtikelResponse;
import com.marmont.artikelservice.service.ArtikelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/artikel") //  api/articles
@RequiredArgsConstructor
public class ArtikelController {

    public final ArtikelService artikelService;

    @PostMapping // POST /api/artikel
    @ResponseStatus(HttpStatus.CREATED)
    public ArtikelResponse createArtikel(@Validated @RequestBody ArtikelRequest artikelRequest) {

        return artikelService.createArtikel(artikelRequest);
    }

    @GetMapping // GET /api/artikel
    @ResponseStatus(HttpStatus.OK)
    public List<ArtikelResponse> getAllArtikel() {

        return artikelService.getAllArtikel();
    }

    @GetMapping("{id}") // GET /api/artikel/42
    @ResponseStatus(HttpStatus.OK)
    public ArtikelResponse getArtikelById(@PathVariable("id") long id) {

        return artikelService.getArtikelById(id);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ArtikelResponse updateArtikel(@PathVariable("id") long id, @RequestBody @Validated ArtikelRequest artikelRequest) {

        return artikelService.updateArtikel(id, artikelRequest);
    }

    @DeleteMapping("{id}")
    public void deleteArtikel(@PathVariable("id") long id) {
        artikelService.markAsDeleted(id);
    }

    @GetMapping("search")
    // GET /api/artikel/search?beschreibung=foo
    // GET /api/artikel/search?ean=123
    // GET /api/artikel/search?ean=123%&beschreibung=bar%
    @ResponseStatus(HttpStatus.OK)
    public List<ArtikelResponse> searchArtikel( //
                                                @RequestParam(value = "beschreibung", required = false) String beschreibung, //
                                                @RequestParam(value = "ean", required = false) String ean //
    ) {

        return artikelService.searchArtikel(beschreibung, ean);
    }


}
