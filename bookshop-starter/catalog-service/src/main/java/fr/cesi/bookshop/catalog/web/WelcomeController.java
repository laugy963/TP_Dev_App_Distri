package fr.cesi.bookshop.catalog.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** TP1 : renvoie app.welcome-message pour observer la précédence des sources de configuration. */
@RestController
@RefreshScope
public class WelcomeController {

    @Value("${app.welcome-message:Bienvenue (valeur par défaut du code)}")
    private String welcomeMessage;

    @GetMapping("/api/welcome")
    public String welcome() {
        return welcomeMessage;
    }
}
