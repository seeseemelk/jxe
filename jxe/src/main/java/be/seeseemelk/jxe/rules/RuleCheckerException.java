package be.seeseemelk.jxe.rules;

import java.util.Optional;

import be.seeseemelk.jxe.FilePosition;

public class RuleCheckerException extends RuntimeException
{
	private static final long serialVersionUID = -6733958147317734195L;
	private final FilePosition position;
	private final Optional<String> longDescription;

	public RuleCheckerException(FilePosition position, String message, String longDescription)
	{
		super(message);
		this.position = position;
		this.longDescription = Optional.of(longDescription);
	}
	
	public RuleCheckerException(FilePosition position, String message)
	{
		super(message);
		this.position = position;
		this.longDescription = Optional.empty();
	}
	
	public FilePosition getPosition()
	{
		return position;
	}
	
	public Optional<String> getLongDescription()
	{
		return longDescription;
	}
}
