#!/bin/sh
set +e
mkdir -p output
rm -vf input/*.class
javac -cp src/main/java input/*.java
