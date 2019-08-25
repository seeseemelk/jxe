package be.seeseemelk.jxe.types;

import java.util.Objects;

public class ClassReference
{
	private final String classFQN;
	
	public ClassReference(String classFQN)
	{
		this.classFQN = classFQN;
	}
	
	public String getClassFQN()
	{
		return classFQN;
	}
	
	@Override
	public String toString()
	{
		return classFQN;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(classFQN);
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
		ClassReference other = (ClassReference) obj;
		return Objects.equals(classFQN, other.classFQN);
	}
}
