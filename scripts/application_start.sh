#!/bin/bash
# Load environment variables from /etc/environment
set -a
source /etc/environment
set +a

echo "Stopping and removing the existing 'nowle-application' container..."
docker stop brevo
docker rm brevo

echo "Removing all Docker images..."
docker rmi $(docker images -q)

docker run -d --name brevo  -p 80:8080 stykle/brevo:latest


