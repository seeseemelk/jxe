#!/bin/sh
set +e
mkdir -p output
rm -vf input/*.class
find input -type f -iname '*.java' -exec javac -cp 'src/main/java:src/jxelib/java' {} \;
