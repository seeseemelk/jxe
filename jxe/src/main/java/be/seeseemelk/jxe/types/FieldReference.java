package be.seeseemelk.jxe.types;

import java.util.Objects;

public class FieldReference
{
	private final ClassReference classReference;
	private final String propertyName;
	
	public FieldReference(ClassReference classReference, String propertyName)
	{
		this.classReference = classReference;
		this.propertyName = propertyName;
	}
	
	public FieldReference(String classFQN, String propertyName)
	{
		this(new ClassReference(classFQN), propertyName);
	}
	
	public ClassReference getClassReference()
	{
		return classReference;
	}
	
	public String getPropertyName()
	{
		return propertyName;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(classReference, propertyName);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldReference other = (FieldReference) obj;
		return Objects.equals(classReference, other.classReference) && Objects.equals(propertyName, other.propertyName);
	}
	
	@Override
	public String toString()
	{
		return classReference.toString() + "/" + propertyName;
	}
}
