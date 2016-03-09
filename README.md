# mechanized-martius

Continuous delivery with Docker for the [Automation Nights Aarhus March 2016 meetup](http://www.meetup.com/CoDe-U-AROS/events/228696565/)


## Overview
This demo shows how to:
* Start Jenkins in a Docker container
* Configure Jenkins at startup
* Use Job DSL to create a CD pipeline
* Use Docker to build and run containers in the pipeline


## Usage
Example commands to try it out, modify as needed:

```sh
# Start Docker host
docker-machine start default
eval $(docker-machine env default)
# Now we can run the 'docker' command

# Start Jenkins
cd jenkins
./build.sh
./run.sh
# Now jenkins is running on http://$(docker-machine ip default):8080
```

Then go to Jenkins in a browser and proceed to:

* Run the seed job
* Run the commit job
* Celebrate


## TODO
* Clean up createJobDsl imports and code
* Job DSL pipeline view, list view
* Release job: push to a Docker registry
* Test job: publishers / plotBuildData using groovy-parsed output.csv file
