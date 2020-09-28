package be.seeseemelk.jtsc.instructions;

import org.objectweb.asm.Type;

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
