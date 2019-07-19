package be.seeseemelk.jxe.types;

import java.util.Objects;

public class ClassImport implements Comparable<ClassImport>
{
	private final String classFqn;
	private final boolean isPublic;
	
	private ClassImport(String classFqn, boolean isPublic)
	{
		this.classFqn = classFqn;
		this.isPublic = isPublic;
	}
	
	public String getClassFqn()
	{
		return classFqn;
	}
	
	public boolean isPublic()
	{
		return isPublic;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(classFqn, isPublic);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof ClassImport))
			return false;
		ClassImport other = (ClassImport) obj;
		return Objects.equals(classFqn, other.classFqn) && isPublic == other.isPublic;
	}

	public static ClassImport makePublic(String classFqn)
	{
		return new ClassImport(classFqn, true);
	}
	
	public static ClassImport makePrivate(String classFqn)
	{
		return new ClassImport(classFqn, false);
	}

	@Override
	public int compareTo(ClassImport o)
	{
		return classFqn.compareTo(o.classFqn);
	}
}
