# BookShop – Starter (TP0)

**CESI – INFAL112 – Développement d'applications distribuées modernes**

Point de départ du projet fil rouge. Vous ferez évoluer ce code **TP après TP** (TP0 → TP12).

Contenu de départ :

| Élément | Port | Description |
|---|---|---|
| `catalog-service` | 8081 | REST `/api/books` (CRUD) + `GET /api/welcome`, base `catalogdb` (JPA, schéma généré par Hibernate) |
| `order-service` | 8082 | REST `POST /api/orders`, `GET /api/orders/{id}`, base `orderdb` ; appelle **en synchrone** catalog-service |
| `docker-compose.yml` | – | Infrastructure complète : PostgreSQL 16, Kafka 3.7 (KRaft), Redis 7, Kafka UI, Adminer |

Volontairement **absents** au départ (ajoutés pendant les TP) : Flyway, Eureka, Config Server, Kafka dans le code, gateway, sécurité, GraphQL, CQRS, saga, Kubernetes…

---

## 1. Prérequis

- JDK **21** (`java -version`) et Maven **3.9+** (`mvn -v`)
- Docker Desktop / Docker Engine avec **Docker Compose v2** (`docker compose version`)
- curl + bash (Windows : Git Bash ou WSL2)
- IDE : IntelliJ IDEA ou VS Code (+ extension *REST Client* pour `http/starter.http`)

## 2. Démarrage pas à pas

```bash
# 1. Infrastructure
docker compose up -d
docker compose ps                 # attendre "healthy" pour postgres, kafka, redis
./scripts/create-topics.sh        # optionnel : crée les topics Kafka utilisés plus tard

# 2. Compilation + tests
mvn -q -DskipTests package
mvn test                          # les tests Testcontainers sont ignorés si Docker est absent

# 3. Services (un terminal chacun, depuis la racine)
mvn -pl catalog-service spring-boot:run
mvn -pl order-service spring-boot:run
```

## 3. Premiers appels

```bash
curl -X POST http://localhost:8081/api/books -H "Content-Type: application/json" \
  -d '{"isbn":"9780132350884","title":"Clean Code","author":"Robert C. Martin","price":15.00}'
curl http://localhost:8081/api/books
curl http://localhost:8081/api/welcome

curl -X POST http://localhost:8082/api/orders -H "Content-Type: application/json" \
  -d '{"bookId":1,"quantity":2,"customerId":"client-42"}'
curl http://localhost:8082/api/orders/1
```

Ou utilisez `http/starter.http` depuis l'IDE.

> **À observer (TP0)** : arrêtez catalog-service puis repassez une commande → `503`. C'est le **couplage temporel** des appels synchrones, que l'on traitera avec la résilience (TP6) puis la saga asynchrone (TP10).

## 4. URLs utiles

| URL | Description | Compte |
|---|---|---|
| http://localhost:8081/api/books | catalog-service | – |
| http://localhost:8082/api/orders | order-service | – |
| http://localhost:8081/actuator/health | santé du service | – |
| http://localhost:8089 | Kafka UI | – |
| http://localhost:8088 | Adminer (Système PostgreSQL, serveur `postgres`) | `bookshop` / `bookshop` |
| `localhost:5432` | PostgreSQL (bases `catalogdb`, `catalogquerydb`, `orderdb`, `paymentdb`) | `bookshop` / `bookshop` |
| `localhost:9092` | Kafka (bootstrap) | – |
| `localhost:6379` | Redis | – |

## 5. Feuille de route des TP

| TP | Ce que vous allez ajouter |
|---|---|
| TP0 | Prise en main du projet & schéma d'architecture |
| TP1 | Sources de configuration, précédence, profils dev/prod, feature flag |
| TP2 | Configuration centralisée (Config Server) + rafraîchissement à chaud via Spring Cloud Bus Kafka |
| TP3 | Découverte de services côté client (Eureka) |
| TP4 | API Gateway + cache de session Redis + ébauche BFF |
| TP5 | Documentation OpenAPI/Swagger + sécurité basique (Basic Auth + HTTPS keystore) |
| TP6 | Résilience : retry, time limiter, rate limiter, circuit breaker |
| TP7 | GraphQL vs REST : serveur GraphQL (schéma, erreurs, versioning) |
| TP8 | Migration de données Flyway + audit des données |
| TP9 | CQRS : catalog command / catalog-query via Kafka |
| TP10 | Saga orchestrée + outbox + transaction compensatoire |
| TP11 | Déploiement Kubernetes (manifestes, ConfigMap, Ingress, suppression d'Eureka) |
| TP12 | Serverless : fonction PSP sur Knative (ou local) |

Le `pom.xml` parent importe déjà la BOM Spring Cloud 2023.0.3 et fixe la version springdoc : il suffira d'ajouter les dépendances dans les modules.

## 6. Dépannage

| Symptôme | Solution |
|---|---|
| `Connection refused localhost:5432` | `docker compose up -d` puis `docker compose ps` |
| `database "catalogdb" does not exist` | Volume créé avant le script d'init : `docker compose down -v && docker compose up -d` (efface les données) |
| Port 5432 / 9092 / 6379 déjà utilisé | Arrêter le service local concurrent ou changer le mapping dans `docker-compose.yml` |
| `422` sur `POST /api/orders` | Le `bookId` n'existe pas dans le catalogue |
| `503` sur `POST /api/orders` | catalog-service est arrêté (voulu pour la démonstration) |
| Windows : `/bin/bash^M: bad interpreter` | Fins de ligne CRLF : `dos2unix scripts/*.sh` |
