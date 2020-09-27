package be.seeseemelk.jtsc.instructions;

import org.objectweb.asm.Label;

public class UnconditionalInstruction extends OpcodeInstruction
{
	private final Label target;

	public UnconditionalInstruction(int opcode, Label target)
	{
		super(opcode);
		this.target = target;
	}
	
	public Label getTarget()
	{
		return target;
	}
}
