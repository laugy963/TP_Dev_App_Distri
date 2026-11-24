package fr.cesi.bookshop.bff;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * BFF de la page d'accueil web : un seul appel renvoie les 5 premiers livres
 * et les 3 dernières commandes. Si un service est en panne, sa partie est vide.
 */
@RestController
public class HomeController {

    private static final ParameterizedTypeReference<List<Map<String, Object>>> LIST =
            new ParameterizedTypeReference<>() { };

    private final RestClient restClient;

    public HomeController(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @GetMapping("/bff/home")
    public Map<String, Object> home() {
        List<Map<String, Object>> books = fetch("http://catalog-service/api/books");
        List<Map<String, Object>> orders = fetch("http://order-service/api/orders");

        Map<String, Object> home = new LinkedHashMap<>();
        home.put("books", books.stream().limit(5).toList());
        home.put("recentOrders", orders.stream().skip(Math.max(0, orders.size() - 3)).toList());
        return home;
    }

    private List<Map<String, Object>> fetch(String url) {
        try {
            List<Map<String, Object>> result = restClient.get().uri(url).retrieve().body(LIST);
            return result == null ? List.of() : result;
        } catch (Exception e) {
            return List.of(); // mode dégradé : la page s'affiche quand même
        }
    }
}
