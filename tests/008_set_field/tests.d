import TestClass : TestClass;

import java.lang.Object;

unittest
{
	auto obj = TestClass.__new();
	obj.set(1337);
	import std.stdio;
	writefln!("Result: %d")(obj.get());
	assert(obj.get() == 1337);
}
