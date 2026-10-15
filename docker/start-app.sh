#!/usr/bin/env bash
# Starts the Toolshop app in Docker, loads the demo data and waits until it is ready.
# Usage: bash docker/start-app.sh
set -e

COMPOSE="docker compose -f docker/docker-compose.yml"

echo "Starting containers..."
$COMPOSE up -d --quiet-pull

echo "Loading demo data (waits for the database to be ready)..."
for i in $(seq 1 30); do
  if $COMPOSE exec -T laravel-api php artisan migrate:fresh --seed --force > /dev/null 2>&1; then
    echo "Demo data loaded."
    break
  fi
  if [ "$i" -eq 30 ]; then
    echo "::error::Could not load demo data."
    $COMPOSE exec -T laravel-api php artisan migrate:fresh --seed --force 2>&1 | tail -15 | sed 's/^/::warning::/'
    exit 1
  fi
  sleep 5
done

echo "Waiting for the API (http://localhost:8091)..."
for i in $(seq 1 60); do
  if curl -sf http://localhost:8091/products > /dev/null; then
    echo "API is up."
    break
  fi
  if [ "$i" -eq 60 ]; then
    echo "::error::API did not start."
    $COMPOSE logs --tail=15 web laravel-api 2>&1 | sed 's/^/::warning::/'
    exit 1
  fi
  sleep 5
done

echo "Waiting for the web app (http://localhost:4200) - the first build takes a minute or two..."
for i in $(seq 1 90); do
  if curl -sf http://localhost:4200 > /dev/null; then
    echo "Web app is up."
    exit 0
  fi
  sleep 5
done

echo "::error::Web app did not start."
$COMPOSE logs --tail=15 angular-ui 2>&1 | sed 's/^/::warning::/'
exit 1
