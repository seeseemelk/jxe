package be.seeseemelk.jtsc.instructions;

public class FieldInstruction extends OpcodeInstruction
{
	private final String owner;
	private final String name;
	private final String descriptor;

	public FieldInstruction(int opcode, String owner, String name, String descriptor)
	{
		super(opcode);
		this.owner = owner;
		this.name = name;
		this.descriptor = descriptor;
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

}
