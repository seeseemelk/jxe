module java.lang.instrumentation;

import java.lang.Object;

union JavaVar
{
	_Object asObject;
}

void aload(ref JavaVar[] vars, _Object object)
{
	JavaVar var;
	var.asObject = object;
	vars ~= var;
}

_Object popObject(ref JavaVar[] vars)
{
	auto obj = vars[$ - 1].asObject;
	vars.length--;
	return obj;
}