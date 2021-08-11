module java.lang.Object;

public
{
	// These packages should always be imported.
	import java.lang.Integer : Integer;
	import java.lang.invoke.LambdaMetafactory : LambdaMetafactory;
	import java.lang.Exception : _Exception, __JavaException;
	import java.lang.ClassCastException : ClassCastException;
}

abstract class _Object : Object
{
	this()
	{
	}

	void __construct()
	{
	}
}

final class Array(T) : _Object
{
	private _Object[] content;

	this()
	{
	}

	this(T[] elements)
	{
		foreach (element; elements)
		{
			content ~= element;
		}
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
		throwJavaException(new ClassCastException());
	return t;
}

/**
Throws a Java exception.
*/
void throwJavaException(_Exception exception)
{
	throw new __JavaException(exception);
}

mixin template autoReflector(T)
{
	import java.lang.Class;

	private static Class _class()
	{
		static Class _class = null;
		if (_class is null)
			_class = new Class;
		return _class;
	}
}
