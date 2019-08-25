package be.seeseemelk.jxe.types;

import java.util.Objects;

import be.seeseemelk.jxe.FilePosition;

public abstract class VariableAccess
{
	public enum Action
	{
		READ, WRITE;
	}
	
	private final Action action;
	private final FilePosition position;
	
	public VariableAccess(FilePosition position, Action action)
	{
		this.position = position;
		this.action = action;
	}
	
	public Action getAction()
	{
		return action;
	}
	
	public FilePosition getPosition()
	{
		return position;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(action);
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
		VariableAccess other = (VariableAccess) obj;
		return action == other.action;
	}
	
}
