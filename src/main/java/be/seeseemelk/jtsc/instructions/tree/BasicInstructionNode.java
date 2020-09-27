package be.seeseemelk.jtsc.instructions.tree;

import be.seeseemelk.jtsc.instructions.Instruction;

public class BasicInstructionNode implements InstructionNode
{
	private final Instruction instruction;
	private InstructionNode nextNode;

	public BasicInstructionNode(Instruction instruction)
	{
		this.instruction = instruction;
	}

	public Instruction getInstruction()
	{
		return instruction;
	}
	
	public boolean hasNext()
	{
		return nextNode != null;
	}
	
	public InstructionNode next()
	{
		return nextNode;
	}

	public void setNext(InstructionNode newNode)
	{
		nextNode = newNode;
	}

}
