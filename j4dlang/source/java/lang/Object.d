module java.lang.Object;

public
{
	// These packages should always be imported.
	import java.lang.Integer : Integer;
	import java.lang.invoke.LambdaMetafactory : LambdaMetafactory;
}

abstract class _Object : Object
{
	this()
	{
	}
}

T[] clone(T)(T[] t)
{
	return t.dup;
}

/**
Attempts to cast `s` to type `T`, throwing an exception on failure.
*/
T checkedCast(T, S)(S s)
{
	if (s is null)
		return null;
	T t = cast(T) s;
	if (t is null)
		throw new Exception("ClassCastException");
	return t;
}

mixin template autoReflector(T)
{
	import java.lang.Class;

	static Class _class;

	private static this()
	{
		_class = new Class;
	}
}
