package fr.cesi.bookshop.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import fr.cesi.bookshop.catalog.book.BookRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/** Test d'intégration avec un vrai PostgreSQL (Testcontainers). Ignoré si Docker est absent. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class BookApiIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate rest;

    @Test
    void create_then_get_book() {
        ResponseEntity<JsonNode> created = rest.postForEntity("/api/books",
                new BookRequest("9991000000001", "Livre de test", "Auteur", new BigDecimal("12.50")), JsonNode.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        long id = created.getBody().get("id").asLong();
        ResponseEntity<JsonNode> fetched = rest.getForEntity("/api/books/" + id, JsonNode.class);
        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetched.getBody().get("title").asText()).isEqualTo("Livre de test");
    }

    @Test
    void unknown_book_returns_404() {
        assertThat(rest.getForEntity("/api/books/999999", String.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void welcome_message_comes_from_application_yml() {
        ResponseEntity<JsonNode> response = rest.getForEntity("/api/welcome", JsonNode.class);
        assertThat(response.getBody().get("message").asText()).isEqualTo("Bienvenue (fichier interne)");
    }
}
