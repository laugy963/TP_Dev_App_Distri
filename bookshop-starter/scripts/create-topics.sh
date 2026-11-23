#!/usr/bin/env bash
# Crée les topics Kafka du projet dans le conteneur "kafka" de docker compose.
# (les services les créent aussi au démarrage via des beans NewTopic ; ce script permet de les voir dès le TP0)
set -euo pipefail
cd "$(dirname "$0")/.."

PARTITIONS="${PARTITIONS:-3}"
TOPICS=(
  "catalog.book-events"
  "saga.catalog.commands"
  "saga.payment.commands"
  "saga.replies"
  "springCloudBus"
)

for topic in "${TOPICS[@]}"; do
  docker compose exec -T kafka /opt/kafka/bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --create --if-not-exists \
    --topic "$topic" \
    --partitions "$PARTITIONS" \
    --replication-factor 1
done

echo
echo "Topics présents :"
docker compose exec -T kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
