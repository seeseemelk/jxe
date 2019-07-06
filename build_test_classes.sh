#!/bin/sh
set +e
mkdir -p output
rm -vf input/*.class
javac input/*.java
