#!/bin/bash

clear
export TIME=$(date +%s)

#create records
echo
echo
echo "*****"
curl -v \
     -H "Content-Type: application/vnd.kafka.json.v2+json" \
     -H "Accept: application/vnd.kafka.v2+json" \
     --data '{"records":[{"key":"jsmith","value":"alarm clock"},{"key":"htanaka","value":"batteries"},{"key":"awalther","value":"bookshelves"}]}' \
     "http://localhost:8082/topics/${TIME}"

#create consumer
echo
echo
echo "*****"
curl -v \
     -H "Content-Type: application/vnd.kafka.v2+json" \
     --data '{"name": "ci1", "format": "json", "auto.offset.reset": "earliest"}' \
     http://localhost:8082/consumers/cg1

#subscribe to topic
echo
echo
echo "*****"
curl -v \
     -H "Content-Type: application/vnd.kafka.v2+json" \
     --data "{\"topics\":[\"${TIME}\"]}" \
     http://localhost:8082/consumers/cg1/instances/ci1/subscription

#get records
echo
echo
echo "*****"
curl -v \
     -H "Accept: application/vnd.kafka.json.v2+json" \
     http://localhost:8082/consumers/cg1/instances/ci1/records

#delete consumer
echo
echo
echo "*****"
curl -v -X DELETE \
     -H "Content-Type: application/vnd.kafka.v2+json" \
     http://localhost:8082/consumers/cg1/instances/ci1
