package be.seeseemelk.jtsc.types;

import java.util.ArrayList;
import java.util.List;

public class DecompiledMethod
{
	private DecompiledClass owner;
	private boolean staticMethod;
	private String name;
	private BaseType returnType;
	private List<BaseType> parameterTypes = new ArrayList<>();
	private List<VariableType> parameterExpressions = new ArrayList<>();
	
	public DecompiledMethod(DecompiledClass owner, String name, String descriptor, boolean staticMethod)
	{
		this.owner = owner;
		this.name = name;
		this.staticMethod = staticMethod;
		
		parseDescriptor(descriptor);
		generateParameters(descriptor);
	}
	
	public DecompiledClass getOwner()
	{
		return owner;
	}

	public String getName()
	{
		return name;
	}

	public BaseType getReturnType()
	{
		return returnType;
	}
	
	/*public void setStaticMethod(boolean staticMethod)
	{
		this.staticMethod = staticMethod;
	}*/
	
	public boolean isStaticMethod()
	{
		return staticMethod;
	}
	
	public String mangleName()
	{
		if (staticMethod)
			return "S" + owner.mangleName() + "_" + name
					.replace("<", "__")
					.replace(">", "__");
		else
			return "M" + owner.mangleName() + "_" + name
					.replace("<", "__")
					.replace(">", "__");
	}
	
	public List<BaseType> getParameterTypes()
	{
		return parameterTypes;
	}
	
	public List<VariableType> getParameterExpressions()
	{
		return parameterExpressions;
	}
	
	public String getMethodDefinition()
	{
		var builder = new StringBuilder();
		builder.append(getReturnType().mangleType() + " " + mangleName() + "(");
		
		if (parameterTypes.isEmpty())
			builder.append("void");
		else
		{
			var parameters = new String[parameterTypes.size()];
			for (int i = 0; i < parameterTypes.size(); i++)
			{
				parameters[i] = parameterTypes.get(i).mangleType() + " " + parameterExpressions.get(i).asValue();
			}
			builder.append(String.join(", ", parameters));
		}
		
		builder.append(")");
		return builder.toString();
	}
	
	private void parseDescriptor(String descriptor)
	{
		this.returnType = BaseType.findType(descriptor.replaceAll("^\\(.*\\)", ""));
	}
	
	private void generateParameters(String descriptor)
	{
		if (!isStaticMethod())
		{
			parameterTypes.add(new InternalType("struct " + owner.mangleType() + "*"));
			parameterExpressions.add(new VariableType("v" + parameterExpressions.size()));
		}
		
		int end = descriptor.lastIndexOf(')');
		for (int i = 1; i < end; i++)
		{
			parameterTypes.add(BaseType.findType(Character.toString(descriptor.charAt(i))));
			parameterExpressions.add(new VariableType("v" + parameterExpressions.size()));
		}
	}
}
