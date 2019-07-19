package be.seeseemelk.jxe.types;

public class InternalType implements BaseType
{
	private String type;
	private String value;
	
	public InternalType(String type, String value)
	{
		this.type = type;
		this.value = value;
	}
	
	public InternalType(String type)
	{
		this.type = type;
	}
	
	public InternalType()
	{
	}
	
	@Override
	public String mangleType()
	{
		return type;
	}
	
	@Override
	public String asValue()
	{
		return value;
	}
}
