#!/usr/bin/env bash

JAR_PATH="$1"
BOOTSTRAP_SERVERS="$2"
TOPIC_NAME="$3"
INPUT_DIR="$4"
SLEEP_INTERVAL_IN_MS="$5"

path='/usr/lib/kafka'

$path/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic "$TOPIC_NAME"
$path/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic etl-topic
$path/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic anomaly-topic

$path/bin/kafka-topics.sh --list --zookeeper localhost:2181

java -jar "$JAR_PATH" "$BOOTSTRAP_SERVERS" "$TOPIC_NAME" "$INPUT_DIR" "$SLEEP_INTERVAL_IN_MS"
