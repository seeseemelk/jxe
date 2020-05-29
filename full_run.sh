#!/bin/sh
set -e
./build_test_classes.sh
./gradlew run
#./test_app.sh
