#!/usr/bin/env bash

path='/usr/lib/kafka'

$path/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --whitelist 'etl-topic|anomaly-topic' --from-beginning \
  --property print.key=true --property print.value=true \
  --formatter kafka.tools.DefaultMessageFormatter