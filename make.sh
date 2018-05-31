#!/bin/sh sh
mvn clean package -Denv=prd -Dmaven.test.skip=true -Djob-name=reportTaskV3