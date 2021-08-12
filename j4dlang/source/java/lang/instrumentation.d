module java.lang.instrumentation;

import java.lang.Object;

union JavaVar
{
	_Object asObject;

	static JavaVar ofObject(_Object obj)
	{
		JavaVar var;
		var.asObject = obj;
		return var;
	}
}
