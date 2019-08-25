package be.seeseemelk.jxe.types;

import java.util.Objects;

import org.objectweb.asm.Type;

import be.seeseemelk.jxe.FilePosition;

public class MethodReference
{
	private final ClassReference classReference;
	private final String methodName;
	private final Type descriptor;
	private final FilePosition position;
	
	public MethodReference(FilePosition position, ClassReference classReference, String methodName, Type descriptor)
	{
		this.position = position;
		this.classReference = classReference;
		this.methodName = methodName;
		this.descriptor = descriptor;
	}
	
	public MethodReference(FilePosition position, String classFQN, String methodName, Type descriptor)
	{
		this(position, new ClassReference(classFQN), methodName, descriptor);
	}
	
	public ClassReference getClassReference()
	{
		return classReference;
	}
	
	public String getMethodName()
	{
		return methodName;
	}
	
	public Type getDescriptor()
	{
		return descriptor;
	}
	
	public FilePosition getPosition()
	{
		return position;
	}
	
	@Override
	public String toString()
	{
		return String.format("%s/%s", classReference.toString(), getMethodName());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(classReference, descriptor, methodName);
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
		MethodReference other = (MethodReference) obj;
		return Objects.equals(classReference, other.classReference) && Objects.equals(descriptor, other.descriptor)
				&& Objects.equals(methodName, other.methodName);
	}
}
