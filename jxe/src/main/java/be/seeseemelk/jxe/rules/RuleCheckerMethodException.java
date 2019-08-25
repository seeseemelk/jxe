package be.seeseemelk.jxe.rules;

import be.seeseemelk.jxe.FilePosition;
import be.seeseemelk.jxe.types.DecompiledMethod;

public class RuleCheckerMethodException extends RuleCheckerException
{
	private static final long serialVersionUID = -3964885255424365127L;
	private final DecompiledMethod method;
	
	public RuleCheckerMethodException(FilePosition position, DecompiledMethod method, String message, String longDescription)
	{
		super(position, message, longDescription);
		this.method = method;
	}

	public RuleCheckerMethodException(FilePosition position, DecompiledMethod method, String message)
	{
		super(position, message);
		this.method = method;
	}
	
	public DecompiledMethod getMethod()
	{
		return method;
	}
	
}
