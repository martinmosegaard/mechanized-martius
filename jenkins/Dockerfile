FROM jenkins:1.642.2
MAINTAINER "martin.amdisen@praqma.com"

# Docker outside of Docker
# http://container-solutions.com/running-docker-in-jenkins-in-docker/
USER root
RUN apt-get update \
    && apt-get install -y sudo \
    && rm -rf /var/lib/apt/lists/*
RUN echo "jenkins ALL=NOPASSWD: ALL" >> /etc/sudoers

USER jenkins

# Install Jenkins plugins
COPY plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/plugins.txt
