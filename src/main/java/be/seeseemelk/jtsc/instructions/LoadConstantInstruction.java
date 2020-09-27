package be.seeseemelk.jtsc.instructions;

public class LoadConstantInstruction extends Instruction
{
	private final Object value;

	public LoadConstantInstruction(Object value)
	{
		this.value = value;
	}
	
	public Object getValue()
	{
		return value;
	}

}
