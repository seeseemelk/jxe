package be.seeseemelk.jxe.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import be.seeseemelk.jxe.Protection;
import be.seeseemelk.jxe.codegen.JavaGenerator;
import be.seeseemelk.jxe.discovery.Flag;
import be.seeseemelk.jxe.references.MethodReference;

public class DecompiledMethod
{
	private transient DecompiledClass owner;
	private boolean staticMethod;
	private final Protection accessor;
	private String name;
	private BaseType returnType;
	private List<BaseType> parameterTypes = new ArrayList<>();
	private List<VariableType> parameterExpressions = new ArrayList<>();
	private List<MethodReference> methodReferences = new ArrayList<>();
	private Set<Flag> flags = EnumSet.noneOf(Flag.class);
	private List<FieldAccess> fieldAccesses = new ArrayList<>();
	
	public DecompiledMethod(DecompiledClass owner, String name, String descriptor, boolean staticMethod, Protection accessor)
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
	
	public boolean isStaticMethod()
	{
		return staticMethod;
	}
	
	public String mangleShortName()
	{
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
	
	private void parseDescriptor(String descriptor)
	{
		int start = descriptor.lastIndexOf(')') + 1;
		returnType = BaseType.findType(descriptor.substring(start)).getValue0();
	}
	
	private void generateParameters(String descriptor)
	{
		if (!isStaticMethod() && !name.equals("<init>"))
		{
			/*parameterTypes.add(new InternalType(owner.mangleType()));
			parameterExpressions.add(new VariableType("v" + parameterExpressions.size()));*/
			parameterTypes.add(new InternalType("this"));
			parameterExpressions.add(new VariableType("v" + parameterExpressions.size()));
		}

		int end = descriptor.lastIndexOf(')');
		if (end > 1)
		{
			for (var parameter : BaseType.findTypes(descriptor.substring(1, end)))
			{
				parameterTypes.add(parameter);
				parameterExpressions.add(new VariableType("v" + parameterExpressions.size()));
			}
		}
	}
	
	public Protection getAccessor()
	{
		return accessor;
	}
	
	public boolean isMain()
	{
		return staticMethod &&
				accessor == Protection.PUBLIC &&
				name.equals("main");
	}
	
	public void addMethodReference(MethodReference reference)
	{
		methodReferences.add(reference);
	}
	
	public Collection<MethodReference> getMethodReferences()
	{
		return Collections.unmodifiableCollection(methodReferences);
	}
	
	public boolean hasFlag(Flag flag)
	{
		return flags.contains(flag);
	}
	
	public void addFlag(Flag flag)
	{
		flags.add(flag);
	}
	
	public void addFieldAccess(FieldAccess fieldAccess)
	{
		fieldAccesses.add(fieldAccess);
	}
	
	public Collection<FieldAccess> getFieldAccesses()
	{
		return Collections.unmodifiableCollection(fieldAccesses);
	}
	
	@Override
	public String toString()
	{
		return JavaGenerator.generateFullFQNMethodDefinition(this);
	}
}








