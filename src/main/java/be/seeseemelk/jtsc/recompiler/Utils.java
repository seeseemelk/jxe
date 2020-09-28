package be.seeseemelk.jtsc.recompiler;

import org.objectweb.asm.Opcodes;

public class Utils
{
	public static String getPackageName(String fqn)
	{
		int index = fqn.lastIndexOf('/');
		if (index == -1)
			index = fqn.lastIndexOf('.');
		if (index == -1)
			return "";
		return fqn.substring(0, index);
	}
	
	public static String getClassName(String fqn)
	{
		String identifier = identifierToD(fqn);
		if (identifier.equals("java.lang.Object"))
			return "_Object";
		
		int index = identifier.lastIndexOf('.');
		return identifier.substring(index + 1);
	}
	
	public static String identifierToD(String text)
	{
		String[] parts = text.replace('/', '.').split("\\.");
		for (int i = 0; i < parts.length; i++)
		{
			String part = parts[i];
			switch (part)
			{
				case "out":
					part = "_out";
					break;
				case "in":
					part = "_in";
					break;
				case "function":
					part = "_function";
					break;
			}
			parts[i] = part;
		}
		return String.join(".", parts)
				.replace("$", "_DOLLAR_");
	}
	
	public static String accessorToString(int accessor)
	{
		switch (accessor & (3))
		{
			case 0:
				System.err.println("Warning: Class has visibility of PACKAGE, using PUBLIC instead");
			case Opcodes.ACC_PUBLIC:
				return "public";
			case Opcodes.ACC_PRIVATE:
				return "private";
			case Opcodes.ACC_PROTECTED:
				return "protected";
			default:
				throw new RuntimeException("Unknown accessor " + accessor);
		}
	}
	
	public static String typeToName(String name)
	{
		switch (name.charAt(0))
		{
			case 'V': return "void";
			case 'Z': return "bool";
			case 'I': return "int";
			case 'F': return "float";
			case '[': return arrayTypeToName(name);
			case 'L': return getClassName(name.substring(1, name.length() - 1));
			default: return name;
			//default: throw new RuntimeException("Unknown type " + name);
		}
	}
	
	private static String arrayTypeToName(String name)
	{
		var base = name.substring(1);
		return typeToName(base) + "[]";
	}
	
	public static boolean isStatic(int accessor)
	{
		return (accessor & Opcodes.ACC_STATIC) != 0;
	}
}
