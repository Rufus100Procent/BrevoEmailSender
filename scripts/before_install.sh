#!/bin/bash

cd /home/ec2-user/
# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed. Installing..."
    sudo yum install -y docker
    sudo service docker start
    sudo systemctl enable docker

    sudo usermod -aG docker ec2-user

    sudo chmod 666 /var/run/docker.sock

    wget https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -O /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
else
    echo "Docker is already installed."
fi

