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
docker run -d --name brevoemail-sender -p 80:8080 stykle/brevo:latest