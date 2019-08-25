package be.seeseemelk.jxe.rules;

import be.seeseemelk.jxe.discovery.Library;
import be.seeseemelk.jxe.types.DecompiledClass;

public abstract class BaseRule
{
	public abstract void checkClass(Library library, DecompiledClass klass);
}
