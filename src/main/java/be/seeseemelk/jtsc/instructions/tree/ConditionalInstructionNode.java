package be.seeseemelk.jtsc.instructions.tree;

import be.seeseemelk.jtsc.instructions.ConditionalInstruction;

public class ConditionalInstructionNode implements InstructionNode
{
	private final ConditionalInstruction instruction;
	private InstructionNode trueBranch;
	private InstructionNode falseBranch;

	public ConditionalInstructionNode(ConditionalInstruction instruction)
	{
		this.instruction = instruction;
	}

	@Override
	public ConditionalInstruction getInstruction()
	{
		return instruction;
	}
	
	public InstructionNode getTrueBranch()
	{
		return trueBranch;
	}
	
	public void setTrueBranch(InstructionNode trueBranch)
	{
		this.trueBranch = trueBranch;
	}
	
	public InstructionNode getFalseBranch()
	{
		return falseBranch;
	}
	
	public void setFalseBranch(InstructionNode falseBranch)
	{
		this.falseBranch = falseBranch;
	}

}
