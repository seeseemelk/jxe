#!/bin/sh
set +e

echo "============================"
echo "Creating fresh build directories"
rm -rf selfhosted extracted
mkdir -p selfhosted/stage1

echo "Building java JXE"
./gradlew shadowJar

echo "Extracting shadow jar"
mkdir extracted
unzip build/libs/jxe-all.jar -d extracted

echo "Removing META-INF"
rm -rf extracted/META-INF

echo "============================"
echo "Building stage 1"
java -jar ./build/libs/jxe-all.jar --only-instrumented extracted selfhosted/stage1
cd selfhosted/stage1
dub build
cd ../..

echo "============================"
echo "Building stage 2"
mkdir -p selfhosted/stage2
java -jar ./selfhosted/stage1/jxe --only-instrumented extracted selfhosted/stage2
cd selfhosted/stage2
dub build
cd ../..

echo "============================"
echo "Building stage 3"
mkdir -p selfhosted/stage3
java -jar ./selfhosted/stage2/jxe --only-instrumented extracted selfhosted/stage3
cd selfhosted/stage3
dub build
cd ../..

echo "============================"
echo "Comparing stage 1 and stage 3"
touch selfhosted/stage1/file
diff -rq selfhosted/stage1 selfhosted/stage3 && echo "Success!" || (echo "Failed!" && false)
