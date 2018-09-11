#!/usr/bin/env bash

mvn clean install docker:build -Ddocker.image.name=sam-ignite-docker
