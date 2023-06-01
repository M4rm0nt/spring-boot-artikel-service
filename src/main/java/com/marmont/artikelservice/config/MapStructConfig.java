package com.marmont.artikelservice.config;

import com.marmont.artikelservice.mapper.ArtikelMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapStructConfig {
    @Bean
    public ArtikelMapper artikelMapper() {
        return Mappers.getMapper(ArtikelMapper.class);
    }
}

