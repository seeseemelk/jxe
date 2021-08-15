import TestClass : TestClass;
import A : A;
import B : B;
import C : C;

import java.lang.Object;
import java.lang.ClassCastException : ClassCastException;

unittest
{
	assert(TestClass.castIntToFloat(2) == 2.0f);
}

unittest
{
	auto b = B.__new();
	auto a = cast(A) b;

	assert(TestClass.castAToB(a) is b);
}

unittest
{
	try
	{
		auto c = C.__new();
		TestClass.castAToB(c);
		assert(0, "Function should have thrown an exception");
	}
	catch (__JavaException e)
	{
		assert(cast(ClassCastException) e.exception !is null);
	}
}

unittest
{
	assert(TestClass.isA(A.__new()) == true);
}

unittest
{
	assert(TestClass.isA(B.__new()) == true);
}

unittest
{
	assert(TestClass.isA(TestClass.__new()) == false);
}
