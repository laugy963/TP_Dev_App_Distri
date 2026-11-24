# Schémas des TP (draw.io)

INFAL112 – Projet bookshop-starter

Chaque TP a un fichier `.drawio` éditable, avec un onglet par schéma, et un export PNG de chaque onglet. Pour modifier un schéma : ouvrir le `.drawio` dans draw.io (application de bureau ou app.diagrams.net), puis réexporter l'onglet en PNG.

Le schéma d'architecture de départ, produit au TP0, reste [../architecture-tp0.drawio](../architecture-tp0.drawio).

Mêmes couleurs dans tous les schémas : catalogue en bleu, commandes en vert, PostgreSQL en jaune, Kafka en violet, Redis en rouge, config-server en orange, annuaire Eureka en turquoise, gateway en violet foncé, BFF en orange foncé.

## TP0 – Prise en main : [tp0-concepts.drawio](tp0-concepts.drawio)

| Onglet | Ce qu'il explique |
|---|---|
| [1. Multi-module et base par service](tp0-1-multi-module-base-par-service.png) | Le parent Maven centralise les versions, et chaque module produit son propre jar. Chaque service est seul maître de sa base : il obtient les données d'un autre service par son API, jamais en lisant sa base. |
| [2. Chemin d'une commande](tp0-2-chemin-commande.png) | Diagramme de séquence de `POST /api/orders` : appel synchrone au catalogue, calcul du total, enregistrement. Il montre aussi les cas d'erreur : 422 si le livre est inconnu, 503 si le catalogue est arrêté (couplage temporel). |
| [3. Twelve-factor](tp0-3-twelve-factor.png) | Les 12 facteurs appliqués au starter (respecté, partiel ou violé), avec le TP qui traite chaque écart, et les trois chantiers suivis pendant le module. |

## TP1 – Configuration : [tp1-configuration.drawio](tp1-configuration.drawio)

| Onglet | Ce qu'il explique |
|---|---|
| [1. Précédence des sources](tp1-1-precedence.png) | Les 7 sources de configuration, de la plus forte à la plus faible, et la grille des scénarios A à E : pour chacun, la source qui gagne et les sources masquées. Également : la conversion d'une propriété en variable d'environnement (relaxed binding) et `/actuator/env`. |
| [2. Profils et feature flag](tp1-2-profils-feature-flag.png) | Comment `application.yml` se combine avec `application-dev.yml` ou `application-prod.yml`. Les beans conditionnels `@Profile("dev")` (DataLoader) et `@ConditionalOnProperty` (DiscountService), et leur vérification par `/actuator/conditions`. |

## TP2 – Configuration centralisée : [tp2-config-server-bus.drawio](tp2-config-server-bus.drawio)

| Onglet | Ce qu'il explique |
|---|---|
| [1. Architecture](tp2-1-architecture-config-server.png) | config-repo, config-server (profil native), deux instances du catalogue, topic Kafka `springCloudBus`. L'ordre des `propertySources`, et les rôles de `optional:` et de `@RefreshScope`. |
| [2. Rechargement à chaud](tp2-2-refresh-a-chaud.png) | Séquence complète, de la modification du fichier jusqu'à la v2 sur les deux instances : `busrefresh`, `RefreshRemoteApplicationEvent`, relecture de la configuration, `Keys refreshed`, accusés de réception. Puis les deux vérifications de robustesse de l'étape 7. |

## TP3 – Découverte de services : [tp3-eureka.drawio](tp3-eureka.drawio)

| Onglet | Ce qu'il explique |
|---|---|
| [1. Architecture Eureka](tp3-1-architecture-eureka.png) | Inscription et heartbeat des instances, téléchargement du registre par order-service, appels répartis par `@LoadBalanced`. Passage d'une adresse en dur à un nom logique. |
| [2. Round robin et perte d'instance](tp3-2-round-robin-perte-instance.png) | Ce que fait l'intercepteur LoadBalancer à chaque appel, la répartition 3/3 mesurée, et la chronologie mesurée après l'arrêt de 8091 : un 503 sur deux pendant environ 33 s, le temps que le cache local du client se rafraîchisse. |

## TP4 – Gateway, session Redis, BFF : [tp4-gateway-session-bff.drawio](tp4-gateway-session-bff.drawio)

| Onglet | Ce qu'il explique |
|---|---|
| [1. Architecture gateway + BFF](tp4-1-architecture-gateway-bff.png) | Les trois routes de la gateway (`Path` vers `lb://`), le BFF qui appelle le catalogue et les commandes, les sessions dans Redis, et l'écart de configuration du endpoint `gateway`. |
| [2. Session dans Redis](tp4-2-session-redis.png) | Session en mémoire comparée à une session dans Redis (gateway avec ou sans état). Les appels mesurés (visits 1, 2, 3, redémarrage, 4) et le contenu du hash Redis. |
| [3. Gateway vs BFF, mode dégradé](tp4-3-gateway-vs-bff-mode-degrade.png) | Tableau comparatif gateway/BFF, séquence de `/bff/home` quand order-service est arrêté, et comportement mesuré : le BFF répond 200, alors que `/api/orders` répond 500 puis 503. |

> **TP1** : l'onglet 2 décrit `DataLoader` et `DiscountService` tels que l'énoncé les demande (étapes 4 et 5). Ces deux classes ne sont pas encore dans le code de catalog-service : seule la propriété `app.features.discount-enabled: false` est présente dans `application.yml`.
