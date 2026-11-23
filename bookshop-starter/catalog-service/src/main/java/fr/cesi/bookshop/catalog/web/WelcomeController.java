package fr.cesi.bookshop.catalog.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Point de départ du TP1 (sources de configuration et précédence). */
@RestController
public class WelcomeController {

    private final String welcomeMessage;

    public WelcomeController(@Value("${app.welcome-message:Bienvenue (valeur par défaut du code)}") String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    @GetMapping("/api/welcome")
    public Map<String, String> welcome() {
        return Map.of("message", welcomeMessage);
    }
}
