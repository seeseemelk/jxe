package be.seeseemelk.jtsc.instructions;

public class IntegerIncrementInstruction extends Instruction
{
	private final int var;
	private final int count;

	public IntegerIncrementInstruction(int var, int count)
	{
		this.var = var;
		this.count = count;
	}
	
	public int getVar()
	{
		return var;
	}
	
	public int getCount()
	{
		return count;
	}

}
