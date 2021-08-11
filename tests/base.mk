JXE ?= java -jar ../../build/libs/jxe.jar
SRC = $(shell find src -iname '*.java')
CLASSES = $(SRC:src/%.java=bin/%.class)

.PHONY: test
test: $(CLASSES)
	$(JXE) $(JAVA_ARGS) bin conv
	cp tests.d conv/source
	cd conv && dub test

bin/%.class: src/%.java
	javac -d bin $^
