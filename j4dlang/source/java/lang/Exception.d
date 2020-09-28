module java.lang.Exception;

import java.lang.Object;

import std.exception;

/**
A wrapper class around Java exception, as D does not
support multiple inheritance.
*/
class __JavaException : Throwable
{
	/// The thrown exception
	public _Exception exception;

	this(_Exception exception)
	{
		super("A Java exception occured");
		this.exception = exception;
	}
}

/**
The base class for all Java exceptions.
*/
class _Exception : _Object
{
	mixin autoReflector!_Exception;

	this()
	{
		super();
	}
}