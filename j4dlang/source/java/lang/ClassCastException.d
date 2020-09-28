module java.lang.ClassCastException;

import java.lang.Object;
import java.lang.Exception : _Exception;

class ClassCastException : _Exception
{
	mixin autoReflector!_Exception;

	this()
	{
		super();
	}
}