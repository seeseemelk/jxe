package be.seeseemelk.jxe.types;

import be.seeseemelk.jxe.Accessor;

public class DecompiledField
{
	private final BaseType type;
	private final Accessor accessor;
	private final String name;
	
	public DecompiledField(BaseType type, Accessor accessor, String name)
	{
		super();
		this.type = type;
		this.accessor = accessor;
		this.name = name;
	}

	public BaseType getType()
	{
		return type;
	}
	
	public Accessor getAccessor()
	{
		return accessor;
	}

	public String getName()
	{
		return name;
	}

}
