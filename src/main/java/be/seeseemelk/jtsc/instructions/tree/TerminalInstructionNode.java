package be.seeseemelk.jtsc.instructions.tree;

import be.seeseemelk.jtsc.instructions.Instruction;

public class TerminalInstructionNode implements InstructionNode
{
	private final Instruction instruction;

	public TerminalInstructionNode(Instruction instruction)
	{
		this.instruction = instruction;
	}

	@Override
	public Instruction getInstruction()
	{
		return instruction;
	}

}
