#!/usr/bin/env bash

path='/usr/lib/kafka'

$path/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic 'kafka.*'
$path/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic 'trips.*'
$path/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic 'etl.*'
$path/bin/kafka-topics.sh --zookeeper localhost:2181 --delete --topic 'anomaly.*'
