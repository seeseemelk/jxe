TESTS = $(filter-out Makefile, $(wildcard *))

.PHONY: test
test: $(TESTS)

.PHONY: $(TESTS)
$(TESTS):
	$(MAKE) -C $@