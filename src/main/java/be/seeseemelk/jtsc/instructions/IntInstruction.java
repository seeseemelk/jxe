package be.seeseemelk.jtsc.instructions;

public class IntInstruction extends OpcodeInstruction
{
	private final int value;

	public IntInstruction(int opcode, int value)
	{
		super(opcode);
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}
}
