FROM golang:1.4

RUN mkdir -p /tmp/build
COPY hello-world.go /tmp/build/
WORKDIR /tmp/build
RUN go build hello-world.go

ENTRYPOINT ./hello-world
