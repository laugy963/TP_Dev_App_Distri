package fr.cesi.bookshop.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * TP4 étape 3 : compteur de visites stocké dans la session.
 * Grâce à spring-session-data-redis, la session vit dans Redis et non dans la mémoire de la gateway.
 */
@RestController
public class SessionInfoController {

    @GetMapping("/session/info")
    public Mono<Map<String, Object>> info(WebSession session) {
        int visits = session.getAttributeOrDefault("visits", 0) + 1;
        session.getAttributes().put("visits", visits);
        return Mono.just(Map.of("sessionId", session.getId(), "visits", visits));
    }
}
