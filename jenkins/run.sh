#!/bin/bash
docker stop mm_jenkins
docker rm -v mm_jenkins
# Docker outside of Docker
# http://container-solutions.com/running-docker-in-jenkins-in-docker/
docker run -d -v /var/run/docker.sock:/var/run/docker.sock \
              -v $(which docker):/usr/bin/docker \
              -p 8080:8080 -p 50000:50000 \
              --name mm_jenkins mechanized-martius/jenkins
echo ==================================
echo Following the logs, press Ctrl+C to quit
echo ==================================
docker logs -f mm_jenkins
