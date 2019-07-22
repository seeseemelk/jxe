package be.seeseemelk.jxe.types;

public class StringType implements BaseType
{
	private final String string;
	
	public StringType(String string)
	{
		this.string = string;
	}

	@Override
	public String mangleType()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public String asValue()
	{
		return '"' + string.replace("\"", "\\\"") + '"';
	}
	
	public String getString()
	{
		return string;
	}

}
