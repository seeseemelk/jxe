package be.seeseemelk.jtsc.instructions;

public class VarInstruction extends OpcodeInstruction
{
	private int var;

	public VarInstruction(int opcode, int var)
	{
		super(opcode);
		this.var = var;
	}

	public int getVar()
	{
		return var;
	}
}
