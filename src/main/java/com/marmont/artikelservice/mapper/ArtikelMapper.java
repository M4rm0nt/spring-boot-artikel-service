package com.marmont.artikelservice.mapper;

import com.marmont.artikelservice.model.Artikel;
import com.marmont.artikelservice.dto.ArtikelRequest;
import com.marmont.artikelservice.dto.ArtikelResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ArtikelMapper {
    ArtikelMapper INSTANCE = Mappers.getMapper(ArtikelMapper.class);

    @Mapping(target = "id", ignore = true)
    Artikel mapToArtikel(ArtikelRequest artikelRequest);

    ArtikelResponse mapToArtikelResponse(Artikel artikel);
}
