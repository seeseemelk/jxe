package be.seeseemelk.jtsc.recompiler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.objectweb.asm.Type;

import be.seeseemelk.jtsc.types.Visibility;

/**
 * Contains information about a method, such as its name, parent class,
 * arguments, and return type.
 */
public class MethodDescriptor
{
	private String name;
	private String className;
	private String returnType;
	private List<String> arguments = Collections.emptyList();
	private boolean isStatic;
	private Visibility visibility;
	
	public void setStatic(boolean isStatic)
	{
		this.isStatic = isStatic; 
	}
	
	public boolean isStatic()
	{
		return isStatic;
	}
	
	public boolean isConstructor()
	{
		return name.equals("<init>");
	}
	
	public boolean isStaticInitializer()
	{
		return name.equals("<clinit>");
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setClassName(String name)
	{
		className = name;
	}
	
	public String getClassName()
	{
		return className;
	}
	
	public void setReturnType(String returnType)
	{
		this.returnType = returnType;
	}
	
	public String getReturnType()
	{
		return returnType;
	}
	
	public void setVisibility(Visibility visibility)
	{
		this.visibility = visibility;
	}
	
	public Visibility getVisibility()
	{
		return visibility;
	}
	
	public void setArguments(List<String> args)
	{
		this.arguments = args;
	}
	
	public List<String> getArguments()
	{
		return arguments;
	}

	public void setArguments(Type[] argumentTypes)
	{
		this.arguments = Stream.of(argumentTypes)
			.map(type -> Utils.typeToName(type.toString()))
			.collect(Collectors.toList());
	}
	
	public void setFromAccess(int access)
	{
		setStatic(Utils.isStatic(access));
		setVisibility(Visibility.fromAccess(access));
	}
}
