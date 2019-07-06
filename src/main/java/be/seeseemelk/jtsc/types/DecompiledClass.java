package be.seeseemelk.jtsc.types;

public class DecompiledClass implements BaseType
{
	private String fullyQualifiedName;
	
	public DecompiledClass(String fullyQualifiedName)
	{
		this.fullyQualifiedName = fullyQualifiedName;
	}
	
	public String getFullyQualifiedName()
	{
		return fullyQualifiedName;
	}
	
	public String getClassName()
	{
		int i = fullyQualifiedName.lastIndexOf('/');
		return fullyQualifiedName.substring(i + 1); 
	}
	
	public String mangleName()
	{
		return fullyQualifiedName.replace('/', '_');
	}
	
	@Override
	public String mangleType()
	{
		return "C" + fullyQualifiedName.replace('/', '_');
	}
}
