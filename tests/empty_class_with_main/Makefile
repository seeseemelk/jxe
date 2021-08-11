SRC = $(shell find src -iname '*.java')
CLASSES = $(SRC:src/%.java=bin/%.class)

.PHONY: test
test: $(CLASSES)
	java -jar ../../build/libs/jtsc.jar $(JAVA_ARGS) bin conv
	cp tests.d conv/source
	cd conv && dub test

bin/%.class: src/%.java
	javac -d bin $^
	