-- Exécuté au premier démarrage du conteneur PostgreSQL (volume vide)
CREATE DATABASE catalogdb;
CREATE DATABASE catalogquerydb;
CREATE DATABASE orderdb;
CREATE DATABASE paymentdb;
GRANT ALL PRIVILEGES ON DATABASE catalogdb TO bookshop;
GRANT ALL PRIVILEGES ON DATABASE catalogquerydb TO bookshop;
GRANT ALL PRIVILEGES ON DATABASE orderdb TO bookshop;
GRANT ALL PRIVILEGES ON DATABASE paymentdb TO bookshop;
