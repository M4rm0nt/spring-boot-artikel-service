package com.marmont.artikelservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marmont.artikelservice.dto.ArtikelRequest;
import com.marmont.artikelservice.model.Artikel;
import com.marmont.artikelservice.repository.ArtikelRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;


import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ArtikelServiceApplicationTests {

	@Container
	static MySQLContainer mysql = new MySQLContainer("mysql:8.0.33");

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ArtikelRepository artikelRepository;

	static {
		mysql.start();
	}

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysql::getJdbcUrl);
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
	}

	@BeforeEach
	public void setup() {
		artikelRepository.deleteAll();
	}

	@AfterEach
	public void cleanup() {
		artikelRepository.deleteAll();
	}

	@Test
	void shouldCreateArtikel() throws Exception {
		ArtikelRequest artikelRequest = getArtikelRequest();
		String artikelRequestString = objectMapper.writeValueAsString(artikelRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/artikel")
						.contentType(MediaType.APPLICATION_JSON)
						.content(artikelRequestString))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.ean", org.hamcrest.Matchers.is(artikelRequest.getEan())));
		Assertions.assertEquals(1, artikelRepository.findAll().size());
	}

	private ArtikelRequest getArtikelRequest() {
		return ArtikelRequest.builder()
				.ean("1234567890123")
				.beschreibung("Artikel Beschreibung")
				.einkaufspreis(BigDecimal.valueOf(50))
				.verkaufspreis(BigDecimal.valueOf(100))
				.build();
	}

	@Test
	void shouldGetAllArtikel() throws Exception {
		Artikel artikel = objectMapper.convertValue(getArtikelRequest(), Artikel.class);
		artikel.setStatus(0);
		artikelRepository.save(artikel);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/artikel")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].status", not(-1)));
	}

	@Test
	void shouldGetArtikelById() throws Exception {
		Artikel artikel = objectMapper.convertValue(getArtikelRequest(), Artikel.class);
		artikel.setStatus(0);
		artikel = artikelRepository.save(artikel);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/artikel/" + artikel.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", org.hamcrest.Matchers.is((int) artikel.getId())))
				.andExpect(jsonPath("$.status", not(-1)));
	}

	@Test
	void shouldUpdateArtikel() throws Exception {
		Artikel artikel = objectMapper.convertValue(getArtikelRequest(), Artikel.class);
		artikelRepository.save(artikel);

		int initialVersion = artikel.getVersion();

		ArtikelRequest artikelRequest = getArtikelRequest();
		artikelRequest.setEan("2345678901234");
		String artikelRequestString = objectMapper.writeValueAsString(artikelRequest);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/artikel/" + artikel.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(artikelRequestString))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.ean", org.hamcrest.Matchers.is(artikelRequest.getEan())));

		Artikel updatedArtikel = artikelRepository.findById(artikel.getId()).orElseThrow();

		Assertions.assertEquals(artikelRequest.getEan(), updatedArtikel.getEan());

		Assertions.assertEquals(initialVersion + 1, updatedArtikel.getVersion());
	}

	@Test
	void shouldMarkArtikelAsDeleted() throws Exception {
		Artikel artikel = artikelRepository.save(objectMapper.convertValue(getArtikelRequest(), Artikel.class));
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/artikel/" + artikel.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		Optional<Artikel> optArtikel = artikelRepository.findById(artikel.getId());
		Assertions.assertTrue(optArtikel.isPresent(), "Artikel should still exist");
		Assertions.assertEquals(-1, optArtikel.get().getStatus(), "Artikel status should be -1");
	}

	@Test
	void shouldSearchArtikel() throws Exception {
		Artikel artikel1 = createArtikel("1234567890123", "Beschreibung 1", 0);
		Artikel artikel2 = createArtikel("2345678901234", "Beschreibung 2", 0);
		Artikel artikel3 = createArtikel("3456789012345", "Beschreibung 3", -1);

		artikelRepository.save(artikel1);
		artikelRepository.save(artikel2);
		artikelRepository.save(artikel3);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/artikel/search")
						.param("ean", artikel1.getEan())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].ean", is(artikel1.getEan())));

		mockMvc.perform(MockMvcRequestBuilders.get("/api/artikel/search")
						.param("beschreibung", artikel2.getBeschreibung())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].beschreibung", is(artikel2.getBeschreibung())));

		mockMvc.perform(MockMvcRequestBuilders.get("/api/artikel/search")
						.param("ean", artikel3.getEan())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	private Artikel createArtikel(String ean, String beschreibung, int status) {
		Artikel artikel = new Artikel();
		artikel.setEan(ean);
		artikel.setBeschreibung(beschreibung);
		artikel.setStatus(status);
		return artikel;
	}

}
