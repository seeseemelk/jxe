TESTS = $(patsubst %/Makefile,%,$(wildcard */Makefile))
SUFFIX ?=

.PHONY: help
help:
	@echo "Test scenarios:"
	@echo "  test              - Run combined (native+instrumented) code-generation tests"
	@echo "  test-instrumented - Run instrumented-only code-generation tests"
	@echo "  test-all          - Run all of the above tests"
	@echo "Tests:" $(TESTS)

.PHONY: test
test: $(TESTS)

.PHONY: test-instrumented
test-instrumented:
	$(MAKE) JAVA_ARGS=--only-instrumented SUFFIX=_instrumented test

.PHONY: test-all
test-all:
	$(MAKE) test
	$(MAKE) test-instrumented

.PHONY: $(TESTS)
$(TESTS): build-jxe
	@echo "   ==== Running test $@ ===="
	$(MAKE) -C $@
	@echo "   ==== Finished test ===="

.PHONY: build-jxe
build-jxe:
	cd .. && ./gradlew jar
