package be.seeseemelk.jxe.rules;

import be.seeseemelk.jxe.discovery.Library;
import be.seeseemelk.jxe.types.DecompiledClass;
import be.seeseemelk.jxe.types.DecompiledMethod;

public abstract class MethodRule extends BaseRule
{
	@Override
	public void checkClass(Library library, DecompiledClass klass)
	{
		klass.getMethods().forEach(method -> checkMethod(library, method));
	}
	
	public abstract void checkMethod(Library library, DecompiledMethod method);
}
