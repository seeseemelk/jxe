package be.seeseemelk.jxe.types;

public class DecompiledClass implements BaseType
{
	private final String fullyQualifiedName;
	private final DecompiledClass owner;
	
	public DecompiledClass(DecompiledClass owner, String fullyQualifiedName)
	{
		this.owner = owner;
		
		if (fullyQualifiedName.startsWith("jxe/"))
			fullyQualifiedName = fullyQualifiedName.substring(4);
		
		if (fullyQualifiedName.charAt(fullyQualifiedName.length() - 1) == ';')
			this.fullyQualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.length() - 1);
		else
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
		return fullyQualifiedName.replace("/", "::");
	}
	
	public String[] getNameParts()
	{
		return mangleName().split("::");
	}
	
	public String[] getNamespaceParts()
	{
		var mangled = mangleName();
		int lastIndex = mangled.lastIndexOf("::");
		return mangled.substring(0, lastIndex).split("::");
	}
	
	@Override
	public String mangleType()
	{
		return mangleName() + "*";
	}
	
	public DecompiledClass getOwner()
	{
		return owner;
	}
	
	public boolean isJavaLangObject()
	{
		return fullyQualifiedName.equals("java/lang/Object");
	}
}
