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

mvn -Dtest=LoaderApplicationTests#testStreamAndUpdateContactWithBinary test

or

mvn -Dtest=LoaderApplicationTests#testStreamAndUpdateContactWithoutBinary test

Problem

We allocate new memory time to time.

`2018-09-11 08:16:51,193  PhysicalMemoryPages: 35481`
`2018-09-11 08:20:21,356  PhysicalMemoryPages: 35591`
