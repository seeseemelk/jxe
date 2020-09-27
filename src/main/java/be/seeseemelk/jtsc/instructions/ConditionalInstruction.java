package be.seeseemelk.jtsc.instructions;

import org.objectweb.asm.Label;

public class ConditionalInstruction extends OpcodeInstruction
{
	private final Label target;

	public ConditionalInstruction(int opcode, Label target)
	{
		super(opcode);
		this.target = target;
	}
	
	public Label getTarget()
	{
		return target;
	}
}
