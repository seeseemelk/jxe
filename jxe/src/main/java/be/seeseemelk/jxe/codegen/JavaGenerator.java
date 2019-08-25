package be.seeseemelk.jxe.codegen;

import java.util.List;
import java.util.stream.Collectors;

import be.seeseemelk.jxe.types.BaseType;
import be.seeseemelk.jxe.types.DecompiledMethod;

public final class JavaGenerator
{
	public static String generateFullMethodDefinition(DecompiledMethod method)
	{
		return String.format("%s %s(%s)",
				method.getReturnType().mangleType(),
				method.getName(),
				generateMethodParameterList(method));
	}
	
	public static String generateFullFQNMethodDefinition(DecompiledMethod method)
	{
		return String.format("%s %s%c%s(%s)",
				method.getReturnType().mangleType(),
				method.getOwner().toString(),
				method.isStaticMethod() ? '.' : '#',
				method.getName(),
				generateMethodParameterList(method));
	}
	
	public static String generateMethodParameterList(DecompiledMethod method)
	{
		List<String> parameters = method.getParameterTypes().stream().map(BaseType::mangleType).collect(Collectors.toList());
		return String.join(", ", parameters);
	}
}
