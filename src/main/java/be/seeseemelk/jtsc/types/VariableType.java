package be.seeseemelk.jtsc.types;

public class VariableType implements BaseType
{
	private String variable;
	
	public VariableType(String variable)
	{
		this.variable = variable;
	}

	@Override
	public String mangleType()
	{
		throw new UnsupportedOperationException("Cannot access type of a variable reference");
	}
	
	@Override
	public String asValue()
	{
		return variable;
	}

}
