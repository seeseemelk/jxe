package be.seeseemelk.jtsc.instructions;

public class TypeInstruction extends OpcodeInstruction
{
	private String type;

	public TypeInstruction(int opcode, String type)
	{
		super(opcode);
		this.type = type;
	}

	public String getType()
	{
		return type;
	}
}
