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
docker run -d --name brevoemail-sender \
  -e EMAIL_USERNAME="$EMAIL_USERNAME" \
  -e EMAIL_PASSWORD="$EMAIL_PASSWORD" \
  -e SENDER_NAME="$SENDER_NAME" \
  -e SENDER_EMAIL="$SENDER_EMAIL" \
  -e BREVO_API_KEY="$BREVO_API_KEY" \
  -e DB_URL="$DB_URL" \
  -e DB_USERNAME="$DB_USERNAME" \
  -e DB_PASSWORD="$DB_PASSWORD" \
  -p 80:8080 stykle/brevo:latest
