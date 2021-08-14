JXE ?= java -jar ../../build/libs/jxe-all.jar
SRC = $(shell find src -iname '*.java')
SUFFIX ?=
CLASSES = $(SRC:src/%.java=bin$(SUFFIX)/%.class)
BUILD_JXE ?= 1

.PHONY: test-normal
test-normal: $(CLASSES) build-jxe
	$(JXE) $(JAVA_ARGS) bin$(SUFFIX) conv$(SUFFIX)
	cp tests.d conv$(SUFFIX)/source
	cd conv$(SUFFIX) && dub test

.PHONY: test-instrumented
test-instrumented: $(CLASSES) build-jxe
	$(JXE) $(JAVA_ARGS) --only-instrumented bin$(SUFFIX) conv$(SUFFIX)
	cp tests.d conv/source
	cd conv && dub test

.PHONY: build-jxe
build-jxe:
ifeq (1,$(BUILD_JXE))
	cd ../.. && ./gradlew shadowJar
endif

bin$(SUFFIX)/%.class: src/%.java
	javac -d bin$(SUFFIX) $^
