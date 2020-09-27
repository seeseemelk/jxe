package be.seeseemelk.jtsc.instructions;

public class MethodCallInstruction extends OpcodeInstruction
{
	private final String owner;
	private final String name;
	private final String descriptor;
	private final boolean isInterface;

	public MethodCallInstruction(int opcode, String owner, String name, String descriptor, boolean isInterface)
	{
		super(opcode);
		this.owner = owner;
		this.name = name;
		this.descriptor = descriptor;
		this.isInterface = isInterface;
	}
	
	public String getOwner()
	{
		return owner;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescriptor()
	{
		return descriptor;
	}
	
	public boolean isInterface()
	{
		return isInterface;
	}

}
