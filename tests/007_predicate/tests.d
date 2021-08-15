import TestClass : TestClass;

import java.lang.Object;

unittest
{
	TestClass.__new();
}

unittest
{
	assert(is(TestClass : _Object));
}
