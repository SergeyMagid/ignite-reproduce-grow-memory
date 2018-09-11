#!/usr/bin/env bash

docker run -it --net=host -v "/tmp/:/config" \
 -e "CONFIG_URI=/config/ignite.xml" \
 -e "OPTION_LIBS=ignite-rest-http,ignite-log4j" \
 -e "JVM_OPTS=-Xms350m -Xmx350m -server -XX:NativeMemoryTracking=summary -XX:+AggressiveOpts -XX:+UseG1GC -XX:+DisableExplicitGC -XX:MaxDirectMemorySize=250M -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dcom.sun.management.jmxremote.port=9918 -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.rmi.port=9918 -Dcom.sun.management.jmxremote.local.only=false -Djava.rmi.server.hostname=192.168.88.191 -Djava.net.preferIPv4Stack=true -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false" \
 -e "IGNITE_H2_DEBUG_CONSOLE=true" -e "IGNITE_QUIET=true" sam-ignite-docker:2.6.0
