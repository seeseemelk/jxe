TESTS = $(sort $(patsubst %/Makefile,%,$(wildcard */Makefile)))
TESTS_INSTRUMENTED = $(TESTS:%=%-instrumented)
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
test-instrumented: $(TESTS_INSTRUMENTED)

.PHONY: test-all
test-all: test test-instrumented

.PHONY: $(TESTS)
$(TESTS): build-jxe
	@echo "   ==== Running test $@ ===="
	$(MAKE) -C $@ BUILD_JXE=0 test-normal
	@echo "   ==== Finished test ===="

.PHONY: $(TESTS_INSTRUMENTED)
$(TESTS_INSTRUMENTED): build-jxe
	@echo "   ==== Running instrumented test $@ ===="
	$(MAKE) -C $(@:%-instrumented=%) BUILD_JXE=0 test-instrumented
	@echo "   ==== Finished instrumented test ===="

.PHONY: build-jxe
build-jxe:
	cd .. && ./gradlew shadowJar
