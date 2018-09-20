# ignite-reproduce-grow-memory



Step to reproduce

Precondition

JAVA 8

Run ignite server part
1. cd ignite-server-docker/
1. Build ignite-server-docker via *./build.sh* script
2. Copy *ignite.xml* to /tmp/ignite.xml
3. Run ignite in docker ./example_run_docker.sh


Run test in ignite-client 
via IDE or maven

cd ignite-client-loader/

mvn -Dtest=LoaderApplicationTests#testStreamAndUpdateContactWithoutBinary test

Problem

We allocate new memory time to time.

https://www.awesomescreenshot.com/image/3619800/ad604d44bae0f241a2618197c2be8af4


Also I have used grafana+ inflxubDb for visualize trend of fillFactor and offHeapSize
( for example https://hub.docker.com/r/samuelebistoletti/docker-statsd-influxdb-grafana/ )
