package be.seeseemelk.jtsc.types;

public class DecompiledField
{
	private final BaseType type;
	private final String name;
	
	public DecompiledField(BaseType type, String name)
	{
		super();
		this.type = type;
		this.name = name;
	}

	public BaseType getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

}
