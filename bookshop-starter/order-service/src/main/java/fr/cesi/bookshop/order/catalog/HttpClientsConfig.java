package fr.cesi.bookshop.order.catalog;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * TP3 : client HTTP « qui connaît l'annuaire ».
 * Avec @LoadBalanced, l'hôte de http://catalog-service n'est pas résolu par le DNS :
 * Spring Cloud LoadBalancer demande à Eureka les instances de catalog-service
 * et en choisit une à chaque appel (round robin).
 */
@Configuration
public class HttpClientsConfig {

    @Bean
    @LoadBalanced
    RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }
}
