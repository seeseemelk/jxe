import TestClass : TestClass;

import java.lang.Object;

unittest
{
	new TestClass;
}

unittest
{
	assert(is(TestClass : _Object));
}
