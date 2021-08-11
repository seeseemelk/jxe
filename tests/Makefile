TESTS = $(filter-out Makefile, $(wildcard *))

.PHONY: help
help:
	@echo "Test scenarios:"
	@echo "  test              - Run combined (native+instrumented) code-generation tests"
	@echo "  test-instrumented - Run instrumented-only code-generation tests"
	@echo "  test-all          - Run all of the above tests"

.PHONY: test
test: $(TESTS)

.PHONY: test-instrumented
test-instrumented:
	$(MAKE) JAVA_ARGS=--only-instrumented test
	
.PHONY: test-all
test-all:
	$(MAKE) test
	$(MAKE) test-instrumented

.PHONY: $(TESTS)
$(TESTS):
	$(MAKE) -C $@