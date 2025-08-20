#!/bin/sh

# Ждем готовности сервера Debezium
until curl -f -s http://debezium:8083/connectors > /dev/null; do
  echo "Waiting for Debezium to start..."
  sleep 5
done

# Регистрируем все коннекторы из папки
for file in /data/connectors/*.json; do
  if [ -f "$file" ]; then
    echo "Registering connector from $file"
    connector_name=$(basename "$file" .json)

    # Проверяем, не зарегистрирован ли уже коннектор
    if curl -f -s "http://debezium:8083/connectors/$connector_name" > /dev/null; then
      echo "Connector $connector_name already exists, updating..."
      curl -X PUT -H "Content-Type: application/json" \
        --data @"$file" \
        "http://debezium:8083/connectors/$connector_name/config"
    else
      echo "Creating new connector $connector_name..."
      curl -X POST -H "Content-Type: application/json" \
        --data @"$file" \
        "http://debezium:8083/connectors"
    fi
    echo ""
  fi
done

echo "All connectors processed"