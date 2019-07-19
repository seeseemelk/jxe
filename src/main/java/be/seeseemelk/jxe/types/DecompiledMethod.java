package be.seeseemelk.jxe.types;

import java.util.ArrayList;
import java.util.List;

import be.seeseemelk.jxe.Accessor;

public class DecompiledMethod
{
	private DecompiledClass owner;
	private boolean staticMethod;
	private final Accessor accessor;
	private String name;
	private BaseType returnType;
	private List<BaseType> parameterTypes = new ArrayList<>();
	private List<VariableType> parameterExpressions = new ArrayList<>();
	
	public DecompiledMethod(DecompiledClass owner, String name, String descriptor, boolean staticMethod, Accessor accessor)
	{
		this.owner = owner;
		this.name = name;
		this.staticMethod = staticMethod;
		this.accessor = accessor;
		
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
	
	public String mangleShortName()
	{
		//return name.replace("<", "__").replace(">", "__");
		return name;
	}
	
	public String mangleLongName()
	{
		return owner.mangleName() + "::" + mangleShortName();
	}
	
	public List<BaseType> getParameterTypes()
	{
		return parameterTypes;
	}
	
	public List<VariableType> getParameterExpressions()
	{
		return parameterExpressions;
	}
	
	private String getModifiers()
	{
		if (isStaticMethod())
			return "static ";
		else
			return "";
	}
	
	public String getShortMethodDefinition()
	{
		var builder = new StringBuilder(getModifiers());
		builder.append(getReturnType().mangleType() + " " + mangleShortName() + "(");
		
		builder.append(getParameterDefinitions());
		
		builder.append(")");
		return builder.toString();
	}

	public String getLongMethodDefinition()
	{
		var builder = new StringBuilder();
		builder.append(getReturnType().mangleType() + " " + mangleLongName() + "(");
		
		builder.append(getParameterDefinitions());
		
		builder.append(")");
		return builder.toString();
	}
	
	public String getParameterDefinitions()
	{
		if (parameterTypes.isEmpty())
			return "";
		else
		{
			var parameters = new String[parameterTypes.size()];
			for (int i = 0; i < parameterTypes.size(); i++)
			{
				parameters[i] = parameterTypes.get(i).mangleType() + " " + parameterExpressions.get(i).asValue();
			}
			return String.join(", ", parameters);
		}
	}
	
	private void parseDescriptor(String descriptor)
	{
		int start = descriptor.lastIndexOf(')') + 1;
		returnType = BaseType.findType(descriptor.substring(start)).getValue0();
	}
	
	private void generateParameters(String descriptor)
	{
		if (!isStaticMethod() && !name.equals("<init>"))
		{
			parameterTypes.add(new InternalType(owner.mangleType()));
			parameterExpressions.add(new VariableType("v" + parameterExpressions.size()));
		}

		int end = descriptor.lastIndexOf(')');
		if (end > 1)
		{
			/*var parameters = descriptor.substring(1, end);
			for (var parameter : parameters.split(";"))
			{
				parameterTypes.add(BaseType.findType(parameter));
				parameterExpressions.add(new VariableType("v" + parameterExpressions.size()));
			}*/
			for (var parameter : BaseType.findTypes(descriptor.substring(1, end)))
			{
				parameterTypes.add(parameter);
				parameterExpressions.add(new VariableType("v" + parameterExpressions.size()));
			}
		}
	}
	
	public String asNamedPointer()
	{
		return String.format("%s(*%s)(%s)", getReturnType().mangleType(), mangleLongName(), getParameterDefinitions());
	}
	
	public Accessor getAccessor()
	{
		return accessor;
	}
	
	public boolean isMain()
	{
		return staticMethod &&
				accessor == Accessor.PUBLIC &&
				name.equals("main");
	}
}
