#!/bin/sh
set -e
mkdir -p output
rm -vf input/*.class
find input -type f -iname '*.java' | xargs javac -cp 'jxelib/src/main/java' -d 'input_class'
