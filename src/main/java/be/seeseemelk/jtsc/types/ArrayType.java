package be.seeseemelk.jtsc.types;

public class ArrayType implements BaseType
{
	private final BaseType base;
	
	public ArrayType(BaseType base)
	{
		this.base = base;
	}

	@Override
	public String mangleType()
	{
		return base.mangleType() + "[]";
	}

}
