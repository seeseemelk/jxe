import TestClass : TestClass;

import java.lang.Object;

unittest
{
	auto obj = TestClass.__new();
	obj.set(1337);
	assert(obj.get() == 1337);
}
