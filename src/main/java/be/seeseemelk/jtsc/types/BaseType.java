package be.seeseemelk.jtsc.types;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

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
	
	public static Pair<BaseType, String> findType(String type)
	{
		int firstSemicolon = type.indexOf(';');
		return switch (type.charAt(0))
		{
			case 'I', 'V', 'Z' -> 
				Pair.with(new PrimitiveType(type.charAt(0)), type.substring(1));
			case 'L' ->
				Pair.with(new DecompiledClass(null, type.substring(0, firstSemicolon)),
						type.substring(firstSemicolon + 1));
			case '[' -> {
				var pair = findType(type.substring(1));
				break Pair.with(new ArrayType(pair.getValue0()), pair.getValue1());
			}
			default -> throw new RuntimeException("Unsupported type '" + type + "'");
		};
	}
	
	public static List<BaseType> findTypes(String type)
	{
		var types = new ArrayList<BaseType>();
		
		while (type.length() > 0)
		{
			/*switch (type.charAt(0))
			{
				case 'I', 'V', 'Z' -> {
					types.add(new PrimitiveType(type.charAt(0)));
					type = type.substring(1);
				}
				case 'L' -> {
					var endPosition = type.indexOf(';');
					types.add(new DecompiledClass(null, type.substring(0, endPosition)));
					type = type.substring(endPosition + 1);
				}
				case '[' -> {
					var endPosition = type.indexOf(';');
					types.add(new ArrayType(findType(type.substring(1))));
					type = type.substring(endPosition + 1);
				}
				default -> {
					throw new RuntimeException("Unsupported type '" + type + "'");
				}
			};*/
			var pair = findType(type);
			types.add(pair.getValue0());
			type = pair.getValue1();
		}
		
		return types;
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
