#!/bin/bash

java -jar rss-datacollection/target/rss-datacollection* > ~/logs/rss-datacollection 2>&1 &
java -jar rss-classification/target/rss-classification* > ~/logs/rss-classification 2>&1 &
java -jar chart-datacollection/target/chart-datacollection* > ~/logs/chart-datacollection 2>&1 &
java -jar rss-insight-prediction/target/rss-insight-prediction* > ~/logs/rss-insight-prediction 2>&1 &
java -jar rss-advisor-prediction/target/rss-advisor-prediction* > ~/logs/rss-advisor-prediction 2>&1 &
java -jar trading-simulation/target/trading-simulation* > ~/logs/trading-simulation 2>&1 &
java -jar microservice-status/target/microservice-status* > ~/logs/microservice-status 2>&1 &
java -jar microservice-authentication/target/microservice-authentication* > ~/logs/microservice-authentication 2>&1 &
java -jar chart-metric/target/chart-metric* > ~/logs/chart-metric 2>&1 &
java -jar chart-metric/target/chart-advisor-prediction* > ~/logs/chart-advisor-prediction 2>&1 &