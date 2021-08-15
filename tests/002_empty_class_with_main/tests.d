import TestClass : TestClass;

import java.lang.Object : _Object, Array;
import java.lang.String : String;

unittest
{
	Array!String args = String.fromArray(["abc", "def"]);
	TestClass.main(args);
	assert(TestClass.strings.length == 2);
	assert(TestClass.strings[0].getDString() == "abc");
	assert(TestClass.strings[1].getDString() == "def");
}
