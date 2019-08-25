package be.seeseemelk.jxe.types;

import java.util.Objects;

import be.seeseemelk.jxe.FilePosition;

public class FieldAccess extends VariableAccess
{
	private final FieldReference reference; 
	private final boolean isStatic;
	
	private FieldAccess(FilePosition position, Action action, FieldReference reference, boolean isStatic)
	{
		super(position, action);
		this.reference = reference;
		this.isStatic = isStatic;
	}
	
	public static FieldAccess withStatic(FilePosition position, Action action, FieldReference reference)
	{
		return new FieldAccess(position, action, reference, true);
	}
	
	public static FieldAccess withObject(FilePosition position, Action action, FieldReference reference)
	{
		return new FieldAccess(position, action, reference, false);
	}
	
	public FieldReference getReference()
	{
		return reference;
	}
	
	public boolean isStatic()
	{
		return isStatic;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(isStatic, reference);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldAccess other = (FieldAccess) obj;
		return isStatic == other.isStatic && Objects.equals(reference, other.reference);
	}
}
