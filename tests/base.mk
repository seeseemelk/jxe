JXE ?= java -jar ../../build/libs/jxe-all.jar
SRC = $(shell find src -iname '*.java')
SUFFIX ?=
CLASSES = $(SRC:src/%.java=bin$(SUFFIX)/%.class)
BUILD_JXE ?= 1

.PHONY: test-normal
test-normal: $(CLASSES) build-jxe
	$(JXE) $(JAVA_ARGS) bin conv
	cp tests.d conv/source
	cd conv && dub test

.PHONY: test-instrumented
test-instrumented: $(CLASSES) build-jxe
	$(JXE) $(JAVA_ARGS) --only-instrumented bin conv_instr
	cp tests.d conv_instr/source
	cd conv_instr && dub test

.PHONY: build-jxe
build-jxe:
ifeq (1,$(BUILD_JXE))
	cd ../.. && ./gradlew shadowJar
endif

bin/%.class: src/%.java
	javac -d bin -cp src $^
