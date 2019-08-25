package be.seeseemelk.jxe.types;

import be.seeseemelk.jxe.Protection;

public class DecompiledField
{
	private final BaseType type;
	private final Protection accessor;
	private final String name;
	
	public DecompiledField(BaseType type, Protection accessor, String name)
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
	
	public Protection getAccessor()
	{
		return accessor;
	}

	public String getName()
	{
		return name;
	}

}
