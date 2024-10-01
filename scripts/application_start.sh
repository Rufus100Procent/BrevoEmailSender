#!/bin/bash
# Load environment variables from /etc/environment
set -a
source /etc/environment
set +a


echo "Stopping and removing any existing containers running 'stykle/brevo'..."

docker ps -a --filter "ancestor=stykle/brevo" --format "{{.ID}}" | xargs -r docker stop
docker ps -a --filter "ancestor=stykle/brevo" --format "{{.ID}}" | xargs -r docker rm
docker images 'stykle/brevo' -q | xargs -r docker rmi

# Run the new container
docker run -d \
  --name myapp_container \
  -e EMAIL_USERNAME="$EMAIL_USERNAME" \
  -e EMAIL_PASSWORD="$EMAIL_PASSWORD" \
  -e SENDER_NAME="$SENDER_NAME" \
  -e SENDER_EMAIL="$SENDER_EMAIL" \
  -e BREVO_API_KEY="$BREVO_API_KEY" \
  -p 8080:8080 \
  myapp:latest