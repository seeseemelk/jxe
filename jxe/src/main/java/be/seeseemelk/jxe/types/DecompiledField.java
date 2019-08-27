package be.seeseemelk.jxe.types;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import be.seeseemelk.jxe.Protection;

public class DecompiledField
{
	private final BaseType type;
	private final int accessIdentifier;
	private final Protection accessor;
	private final String name;
	
	public DecompiledField(BaseType type, int accessIdentifier, String name)
	{
		super();
		this.type = type;
		this.accessIdentifier = accessIdentifier;
		this.accessor = Protection.fromProtectionInt(accessIdentifier);
		this.name = name;
	}

	public BaseType getType()
	{
		return type;
	}
	
	public Protection getAccessor()
	{
		return accessor;
	}

	public String getName()
	{
		return name;
	}
	
	public boolean isFinal()
	{
		return Protection.isFinal(accessIdentifier);
	}
	
	public boolean isStatic()
	{
		return Protection.isStatic(accessIdentifier);
	}

	@Override
	public String toString()
	{
		return String.format("DecompiledField{name: %s, type: %s}", name, type.toString());
	}
}
