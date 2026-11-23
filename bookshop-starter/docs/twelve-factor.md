# BookShop au TP0 — analyse « twelve-factor »

Constat sur le starter (catalog-service + order-service + docker-compose).
Ces notes servent de fil conducteur : chaque écart est repris par un TP.

| # | Facteur | Élément du projet | Verdict | Repris au |
|---|---|---|---|---|
| I | **Codebase** | Un seul dépôt Git (monorepo Maven) pour 2 applications déployées séparément (un jar par module), bientôt 9. La règle stricte est « une app = un codebase ». | Partiel | Choix assumé du module |
| II | **Dependencies** | Toutes les dépendances sont déclarées dans les `pom.xml`. Leurs versions sont fixées par `spring-boot-starter-parent` 3.3.5 et la BOM Spring Cloud 2023.0.3. Le jar est autonome (Tomcat embarqué). Bémol : sans Maven Wrapper (`mvnw`), Maven doit être installé à part sur le poste. | Respecté | — |
| III | **Config** | L'URL JDBC, les identifiants `bookshop`/`bookshop` et `app.catalog.url: http://localhost:8081` sont écrits en dur dans `application.yml`, donc dans le jar et dans Git. On peut les surcharger par variables d'environnement (`SPRING_DATASOURCE_URL`, `APP_CATALOG_URL`), mais rien n'est prévu pour. | **Violé** | TP1, TP2, TP11 |
| IV | **Backing services** | PostgreSQL, Kafka et Redis sont des ressources attachées, désignées par une URL. Passer à un PostgreSQL managé ne demande de changer que la configuration, pas le code. Pour order-service, catalog-service est lui aussi une ressource attachée. | Respecté | TP3 (URL du catalogue) |
| V | **Build, release, run** | Le build (`mvn package`) produit un jar. Mais il n'y a pas de *release* identifiable : version `1.0.0-SNAPSHOT` et configuration embarquée dans le jar. `mvn spring-boot:run` compile et exécute en une seule étape. | Partiel | TP11 (image + manifeste) |
| VI | **Processes** | Les services sont sans état : les livres et les commandes sont en base, sans session HTTP ni cache local (`open-in-view: false`). | Respecté | TP4 (sessions dans Redis) |
| VII | **Port binding** | Chaque service expose HTTP via son Tomcat embarqué (8081, 8082), sans serveur d'applications externe. Le port peut être surchargé par `SERVER_PORT`. | Respecté | — |
| VIII | **Concurrency** | Comme les processus sont sans état, on pourrait en théorie les dupliquer. En pratique, c'est bloqué : order-service ne connaît qu'une seule instance du catalogue (`localhost:8081`). | Partiel | TP3 (Eureka + répartition de charge), TP11 (replicas) |
| IX | **Disposability** | Le démarrage est rapide (environ 3 s mesurées) et l'arrêt sur SIGTERM est géré par Spring. Mais aucun arrêt gracieux n'est configuré (`server.shutdown`, qui vaut `immediate` par défaut en Boot 3.3). L'appel au catalogue n'a ni timeout ni retry : un catalogue lent bloque les commandes. | Partiel | TP6 (time limiter, retry, circuit breaker) |
| X | **Dev/prod parity** | Points positifs : Docker Compose fournit les mêmes services de support qu'en production, et les tests utilisent un vrai PostgreSQL via Testcontainers (pas de H2). Écarts : les services tournent hors conteneur, le schéma est géré par Hibernate, et certaines images ne sont pas figées (`kafka-ui:latest`, `adminer` sans tag, `postgres:16` et `redis:7` qui suivent la dernière version mineure). | Partiel | TP8 (Flyway), TP11 |
| XI | **Logs** | Les logs partent sur la sortie standard, sans fichier configuré. C'est l'environnement qui les collecte (terminal, `docker logs`, puis `kubectl logs`). | Respecté | — |
| XII | **Admin processes** | Les tâches d'administration ne sont pas versionnées avec l'application. Les bases sont créées par `init.sql`, qui ne s'exécute que si le volume est vide (d'où l'erreur `database "catalogdb" does not exist`). Les topics sont créés par un script shell. Le schéma est créé par `ddl-auto: update` à chaque démarrage. | **Violé** | TP8 (migrations Flyway) |

## Les trois chantiers à suivre pendant les quatre jours

1. **Configuration externalisée (III)** : fichier interne → profils → Config Server → ConfigMap/Secret (TP1, TP2, TP11).
2. **Appel synchrone fragile (VIII, IX)** : URL en dur et aucune protection → Eureka, résilience, puis saga asynchrone (TP3, TP6, TP10).
3. **Schéma et données (X, XII)** : `ddl-auto: update` et `init.sql` → migrations Flyway versionnées (TP8).
