import TestClass : TestClass;

import java.lang.Object;

unittest
{
	assert(TestClass.count(0) == 0);
}

unittest
{
	assert(TestClass.count(2) == 3);
}