package fr.cesi.bookshop.order.catalog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * Appel SYNCHRONE du catalogue (TP0 : couplage temporel à discuter).
 * L'URL est en dur dans la configuration : la découverte de services arrive au TP3,
 * l'appel synchrone sera remplacé par la saga Kafka au TP10.
 */
@Component
public class CatalogClient {

    private final RestClient restClient;

    public CatalogClient(RestClient.Builder builder, @Value("${app.catalog.url:http://localhost:8081}") String catalogUrl) {
        this.restClient = builder.baseUrl(catalogUrl).build();
    }

    public Optional<BookDto> findBook(Long bookId) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri("/api/books/{id}", bookId)
                    .retrieve()
                    .body(BookDto.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }
}
