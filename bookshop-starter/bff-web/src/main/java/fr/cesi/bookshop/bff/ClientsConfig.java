package fr.cesi.bookshop.bff;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Même principe qu'au TP3 : un RestClient.Builder @LoadBalanced résout
 * http://catalog-service et http://order-service via l'annuaire Eureka.
 */
@Configuration
public class ClientsConfig {

    @Bean
    @LoadBalanced
    RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }
}
