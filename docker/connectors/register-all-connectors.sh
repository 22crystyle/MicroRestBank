#!/bin/sh

# Function to register a single connector file
register_connector() {
  file=$1
  echo "Processing connector configuration from $file"
  connector_name=$(basename "$file" .json)

  # Check if the connector already exists
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
}


# Wait for Debezium to be ready
until curl -f -s http://debezium:8083/connectors > /dev/null; do
  echo "Waiting for Debezium to start..."
  sleep 5
done

# Process all connector templates
for template_file in /data/connectors/*.json.template; do
  if [ -f "$template_file" ]; then
    processed_json_file="/tmp/$(basename "$template_file" .template)"
    # Substitute variables from environment
    envsubst < "$template_file" > "$processed_json_file"
    register_connector "$processed_json_file"
    rm "$processed_json_file"
  fi
done

# Process all static connectors
for file in /data/connectors/*.json; do
  if [ -f "$file" ]; then
    register_connector "$file"
  fi
done

echo "All connectors processed"
