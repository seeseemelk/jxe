package be.seeseemelk.jtsc.types;

public interface BaseType
{
	/*private Object type;
	
	public BaseType(Object type)
	{
		this.type = type;
	}*/
	
	/*public String getType()
	{
		return type;
	}*/
	
	/*public String mangleType()
	{
		if (type instanceof String)
		{
			switch ((String) type)
			{
				case "I": return "int";
				case "V": return "void";
				default: throw new RuntimeException("Unsupported type");
			}
		}
		else if (type instanceof DecompiledClass)
			return ((DecompiledClass) type).mangleName();
		else
			throw new RuntimeException("Unsupported type");
	}*/
	
	String mangleType();
	
	default String asValue()
	{
		throw new UnsupportedOperationException("Cannot access a " + getClass().getSimpleName() + " as a value");
	}
	
	public static BaseType findType(String type)
	{
		if (type.length() == 1)
			return new PrimitiveType(type);
		else
			throw new RuntimeException("Unsupported type '" + type + "'");
	}
	
	/*public String asVariableString()
	{
		throw new UnsupportedOperationException();
	}
	
	public String asAssignmentString()
	{
		throw new UnsupportedOperationException();
	}
	
	public String asExpressionString()
	{
		throw new UnsupportedOperationException();
	}*/
}
