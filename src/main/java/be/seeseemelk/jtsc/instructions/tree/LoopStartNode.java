package be.seeseemelk.jtsc.instructions.tree;

import be.seeseemelk.jtsc.instructions.Instruction;

public class LoopStartNode implements InstructionNode
{
	private Instruction body;

	public LoopStartNode(Instruction body)
	{
		this.body = body;
	}

	@Override
	public Instruction getInstruction()
	{
		return body;
	}

}
