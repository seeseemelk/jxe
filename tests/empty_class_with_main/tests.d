import TestClass : TestClass;

import java.lang.Object : _Object;
import java.lang.String : String;

unittest
{
	TestClass.main([new String("abc"), new String("def")]);
	assert(TestClass.strings.length == 2);
	assert(TestClass.strings[0].getDString() == "abc");
	assert(TestClass.strings[1].getDString() == "def");
}
