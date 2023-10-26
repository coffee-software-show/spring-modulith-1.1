#!/usr/bin/env bash
rm -rf target &&  ./mvnw -Pnative -DskipTests clean package native:compile  && ./target/moduliths
