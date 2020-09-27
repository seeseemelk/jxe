package be.seeseemelk.jtsc.instructions;

public class OpcodeInstruction extends Instruction
{
	private final int opcode;
	
	public OpcodeInstruction(int opcode)
	{
		this.opcode = opcode;
	}
	
	public int getOpcode()
	{
		return opcode;
	}
}
