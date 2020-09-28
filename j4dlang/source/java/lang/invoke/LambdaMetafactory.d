module java.lang.invoke.LambdaMetafactory;

import java.lang.Object;
import java.util._function.Predicate : Predicate;

class LambdaMetafactory : _Object
{
	mixin autoReflector!LambdaMetafactory;

	static Predicate delegate() metafactory(bool delegate(_Object) callback)
	{
		return () =>
			new class Predicate
			{
				override bool test(_Object obj)
				{
					return callback(obj);
				}
			};
	}
}
