module java.lang.instrumentation;

import java.lang.Object;

struct JavaVar
{
	enum Type
	{
		OBJECT,
		INT,
		FLOAT
	}

	union
	{
		_Object asObject;
		int asInt;
		float asFloat;
	}
	Type type;


	static JavaVar ofObject(_Object obj)
	{
		JavaVar var;
		var.asObject = obj;
		var.type = Type.OBJECT;
		return var;
	}

	static JavaVar ofInt(int value)
	{
		JavaVar var;
		var.asInt = value;
		var.type = Type.INT;
		return var;
	}

	static JavaVar ofFloat(float value)
	{
		JavaVar var;
		var.asFloat = value;
		var.type = Type.FLOAT;
		return var;
	}
}
