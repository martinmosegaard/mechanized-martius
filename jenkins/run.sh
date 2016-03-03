#!/bin/bash
docker stop mechanized_martius
docker rm mechanized_martius
docker run -d -v /var/run/docker.sock:/var/run/docker.sock \
              -v $(which docker):/usr/bin/docker \
              -p 8080:8080 -p 50000:50000 \
              --name mechanized_martius mechanized-martius/jenkins
