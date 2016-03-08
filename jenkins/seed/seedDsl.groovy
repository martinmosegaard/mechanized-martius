/**
 */

job('commit') {

  scm {
    github('martinmosegaard/mechanized-martiues')
  }

  steps {
    shell('''\
    #!/bin/bash -x
    sudo docker rm -f testing-app

    cd docker
    sudo docker build -t testing-app:snapshot .
    cid=$(sudo docker run -d --name testing-app testing-app:snapshot)
    cip=$(sudo docker inspect --format '{{.NetworkSettings.IPAddress}}' ${cid})

    # Unit test result
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
