import TestClass : TestClass;

import java.lang.Object;

// isPositive
unittest
{
	assert(TestClass.isPositive(5).getDString == "yes");
}

unittest
{
	assert(TestClass.isPositive(-5).getDString == "no");
}

unittest
{
	assert(TestClass.isPositive(0).getDString == "yes");
}

// isNegative
unittest
{
	assert(TestClass.isNegative(5).getDString == "no");
}

unittest
{
	assert(TestClass.isNegative(-5).getDString == "yes");
}

unittest
{
	assert(TestClass.isNegative(0).getDString == "no");
}

// isFive
unittest
{
	assert(TestClass.isFive(5).getDString == "yes");
}

unittest
{
	assert(TestClass.isFive(4).getDString == "no");
}
