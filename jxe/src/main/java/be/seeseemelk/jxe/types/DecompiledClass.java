package be.seeseemelk.jxe.types;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DecompiledClass implements BaseType
{
	public static final DecompiledClass OBJECT = new DecompiledClass();
	private final String fullyQualifiedName;
	private final Optional<DecompiledClass> parent;
	private final List<DecompiledMethod> methods = new ArrayList<>();
	private final List<DecompiledField> fields = new ArrayList<>();
	
	/**
	 * Creates a decompiled class with a parent.
	 * @param parent The parent of the class.
	 * @param fullyQualifiedName The FQN of this class.
	 */
	public DecompiledClass(DecompiledClass parent, String fullyQualifiedName)
	{
		this.parent = Optional.of(parent);
		this.fullyQualifiedName = parseFQN(fullyQualifiedName);
	}
	
	/**
	 * Creates a decompiled class without a parent class.
	 * @param fullyQualifiedName
	 */
	public DecompiledClass(String fullyQualifiedName)
	{
		this.parent = Optional.empty();
		this.fullyQualifiedName = parseFQN(fullyQualifiedName);
	}
	
	/**
	 * Creates the {@code java.lang.Object} class.
	 */
	private DecompiledClass()
	{
		this.parent = Optional.of(this);
		this.fullyQualifiedName = "java/lang/Object";
	}
	
	private static String parseFQN(String fqn)
	{
		if (fqn.startsWith("jxe/"))
			fqn = fqn.substring(4);
		
		if (fqn.charAt(fqn.length() - 1) == ';')
			return fqn.substring(0, fqn.length() - 1);
		else
			return fqn;
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
		return fullyQualifiedName.replace('/', '.');
	}
	
	public String[] getNameParts()
	{
		return mangleName().split(".");
	}
	
	public String[] getNamespaceParts()
	{
		var mangled = mangleName();
		int lastIndex = mangled.lastIndexOf('.');
		return mangled.substring(0, lastIndex).split(".");
	}
	
	@Override
	public String mangleType()
	{
		return mangleName();
	}
	
	public Optional<DecompiledClass> getParent()
	{
		return parent;
	}
	
	public boolean isJavaLangObject()
	{
		return fullyQualifiedName.equals("java/lang/Object");
	}
	
	public void addMethod(DecompiledMethod method)
	{
		methods.add(method);
	}
	
	public List<DecompiledMethod> getMethods()
	{
		return Collections.unmodifiableList(methods);
	}
	
	public void addField(DecompiledField field)
	{
		fields.add(field);
	}
	
	public List<DecompiledField> getFields()
	{
		return Collections.unmodifiableList(fields);
	}
	
	@Override
	public String toString()
	{
		return getFullyQualifiedName().replace("/", ".");
	}
}
