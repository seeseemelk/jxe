import TestClass : TestClass;

import java.lang.Object : _Object;
import java.lang.String : String;

unittest
{
	assert(is(typeof(TestClass.name) == String));
}
