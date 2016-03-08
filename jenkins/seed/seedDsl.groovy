job('commit') {
  description('Build and smoke test')

  scm {
    github('martinmosegaard/mechanized-martius')
  }

  steps {
    shell('''\
    #!/bin/bash -x
    sudo docker rm -f testing-app

    # Build and run a the web server in Docker
    cd docker
    sudo docker build -t testing-app:snapshot .
    sudo docker run -d --name testing-app testing-app:snapshot
    cip=$(sudo docker inspect --format '{{.NetworkSettings.IPAddress}}' testing-app)

    # Smoke test: Is the web server reachable?
    sudo docker run --rm rufus/siege-engine -g http://${cip}:80/
    if [ $? -ne 0 ]
    then
      echo Unit test failed
      exit 1
    else
      echo Unit test passed
    fi
    '''.stripIndent())
  }
}

job('test') {
  description('Acceptance test')

  steps {
    shell('''\
    #!/bin/bash -x

    # Acceptance test: Stress test for 10 seconds
    cip=$(sudo docker inspect --format '{{.NetworkSettings.IPAddress}}' testing-app)
    sudo docker run --rm rufus/siege-engine -t 10s http://${cip}:80/ > output 2>&1

    # Acceptance test result test: Was availability 100%?
    avail=$(cat output | grep Availability)
    echo $avail
    if [[ "$avail" == *"100.00"* ]]
    then
    	echo "Availability high enough"
    else
    	echo "Availability too low"
    	exit 1
    fi
    '''.stripIndent())
  }
}
