#!/bin/bash

docker compose up -d

export BOOTSTRAP_SERVERS='broker:29092'
export COMPOSE_IGNORE_ORPHANS=True

docker compose exec broker \
  kafka-topics --create \
    --topic foobar \
    --bootstrap-server localhost:9092 \
    --replication-factor 1 \
    --partitions 1

docker compose -f rest-proxy.yml up -d

docker compose -f rest-proxy.yml logs rest-proxy | grep "Server started, listening for requests"
