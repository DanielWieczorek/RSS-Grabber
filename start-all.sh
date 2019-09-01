#!/bin/bash

java -XX:MaxHeapFreeRatio=20 -XX:MinHeapFreeRatio=10 -jar rss-datacollection/target/rss-datacollection* > ~/logs/rss-datacollection 2>&1 &
java -XX:MaxHeapFreeRatio=20 -XX:MinHeapFreeRatio=10 -jar rss-classification/target/rss-classification* > ~/logs/rss-classification 2>&1 &
java -XX:MaxHeapFreeRatio=20 -XX:MinHeapFreeRatio=10 -jar chart-datacollection/target/chart-datacollection* > ~/logs/chart-datacollection 2>&1 &
java -XX:MaxHeapFreeRatio=20 -XX:MinHeapFreeRatio=10 -jar rss-insight-prediction/target/rss-insight-prediction* > ~/logs/rss-insight-prediction 2>&1 &
java -XX:MaxHeapFreeRatio=20 -XX:MinHeapFreeRatio=10 -jar rss-advisor-prediction/target/rss-advisor-prediction* > ~/logs/rss-advisor-prediction 2>&1 &
java -XX:MaxHeapFreeRatio=20 -XX:MinHeapFreeRatio=10 -jar trading-simulation/target/trading-simulation* > ~/logs/trading-simulation 2>&1 &
java -XX:MaxHeapFreeRatio=20 -XX:MinHeapFreeRatio=10 -jar microservice-status/target/microservice-status* > ~/logs/microservice-status 2>&1 &
java -XX:MaxHeapFreeRatio=20 -XX:MinHeapFreeRatio=10 -jar microservice-authentication/target/microservice-authentication* > ~/logs/microservice-authentication 2>&1 &
java -XX:MaxHeapFreeRatio=20 -XX:MinHeapFreeRatio=10 -jar chart-metric/target/chart-metric* > ~/logs/chart-metric 2>&1 &
java -XX:MaxHeapFreeRatio=20 -XX:MinHeapFreeRatio=10 -jar chart-advisor-prediction/target/chart-advisor-prediction* > ~/logs/chart-advisor-prediction 2>&1 &