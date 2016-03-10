job('commit') {
  description('Build and smoke test')

  scm {
    github('martinmosegaard/mechanized-martius')
  }

  steps {
    shell('''\
    #!/bin/bash -x
    sudo docker rm -f shamu-app

    # Build and run a the web server in Docker
    cd docker
    sudo docker build -t praqma/shamu-app:snapshot .
    sudo docker run -d --name shamu-app praqma/shamu-app:snapshot
    shamuAppIp=$(sudo docker inspect --format '{{.NetworkSettings.IPAddress}}' shamu-app)

    # Smoke test: Is the web server reachable?
    sudo docker run --rm rufus/siege-engine -g http://${shamuAppIp}:80/
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

  triggers {
    upstream('commit')
  }

  steps {
    shell('''\
    #!/bin/bash -x

    # Acceptance test: Stress test for 10 seconds
    shamuAppIp=$(sudo docker inspect --format '{{.NetworkSettings.IPAddress}}' shamu-app)
    sudo docker run --rm rufus/siege-engine -t 10s http://${shamuAppIp}:80/ > output 2>&1

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

job('release') {

  description('Release the artifacts')

  triggers {
    upstream('test')
  }

  steps {
    shell('''\
    #!/bin/bash -x

    sudo docker tag praqma/shamu-app:snapshot praqma/shamu-app:stable
    '''.stripIndent())
  }
}
