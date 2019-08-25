package be.seeseemelk.jxe.types;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.objectweb.asm.Type;

public interface BaseType
{	
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
			case 'I', 'V', 'Z', 'B', 'J', 'D', 'C', 'S', 'F' -> 
				Pair.with(new PrimitiveType(type.charAt(0)), type.substring(1));
			case 'L' ->
				Pair.with(new DecompiledClass(type.substring(1, firstSemicolon)),
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
			var pair = findType(type);
			types.add(pair.getValue0());
			type = pair.getValue1();
		}
		
		return types;
	}
}
