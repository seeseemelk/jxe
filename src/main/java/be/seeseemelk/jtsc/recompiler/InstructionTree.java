package be.seeseemelk.jtsc.recompiler;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.instructions.ConditionalInstruction;
import be.seeseemelk.jtsc.instructions.Instruction;
import be.seeseemelk.jtsc.instructions.OpcodeInstruction;
import be.seeseemelk.jtsc.instructions.UnconditionalInstruction;
import be.seeseemelk.jtsc.instructions.tree.BasicInstructionNode;
import be.seeseemelk.jtsc.instructions.tree.ConditionalInstructionNode;
import be.seeseemelk.jtsc.instructions.tree.InstructionNode;
import be.seeseemelk.jtsc.instructions.tree.TerminalInstructionNode;

public class InstructionTree
{
	private InstructionNode start;

	private InstructionTree()
	{
	}
	
	public InstructionNode getStart()
	{
		return start;
	}
	
	public static InstructionTree from(InstructionStream stream) throws FlowAnalysisException
	{
		var tree = new InstructionTree();
		tree.start = nodesFrom(stream, 0);
		return tree;
	}

	private static InstructionNode nodesFrom(
			InstructionStream stream,
			int index
	) throws FlowAnalysisException
	{
		Instruction instr = stream.get(index);
		if (isReturn(instr))
		{
			var node = new TerminalInstructionNode(instr);
			return node;
		}
		else if (instr instanceof ConditionalInstruction)
		{
			var cond = (ConditionalInstruction) instr;
			int target = stream.getIndex(cond.getTarget());

			var node = new ConditionalInstructionNode(cond);
			node.setTrueBranch(nodesFrom(stream, target));
			node.setFalseBranch(nodesFrom(stream, index + 1));
			return node;
		}
		else if (instr instanceof UnconditionalInstruction)
		{
			var jump = (UnconditionalInstruction) instr;
			int target = stream.getIndex(jump.getTarget());
			if (target > index)
			{
				return nodesFrom(stream, target);
			}
			else
			{
				throw new FlowAnalysisException("Jump from index " + index + " to " + target);
			}
		}
		else
		{
			var node = new BasicInstructionNode(instr);
			node.setNext(nodesFrom(stream, index + 1));
			return node;
		}
	}
	
	private static boolean isReturn(Instruction instruction)
	{
		if (instruction instanceof OpcodeInstruction)
		{
			var opcode = ((OpcodeInstruction) instruction).getOpcode();
			return opcode == Opcodes.RETURN
			    || opcode == Opcodes.IRETURN
			    || opcode == Opcodes.LRETURN
			    || opcode == Opcodes.FRETURN
			    || opcode == Opcodes.DRETURN
			    || opcode == Opcodes.ARETURN;
		}
		return false;
	}

}
