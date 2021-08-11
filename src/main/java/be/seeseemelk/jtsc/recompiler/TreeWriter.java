package be.seeseemelk.jtsc.recompiler;

import be.seeseemelk.jtsc.decoders.ntv.NativeFieldInsnDecoder;
import be.seeseemelk.jtsc.decoders.ntv.NativeInsnDecoder;
import be.seeseemelk.jtsc.decoders.ntv.NativeIntInsnDecoder;
import be.seeseemelk.jtsc.decoders.ntv.NativeJumpInsnDecoder;
import be.seeseemelk.jtsc.decoders.ntv.NativeLdcInsnDecoder;
import be.seeseemelk.jtsc.decoders.ntv.NativeMethodInsnDecoder;
import be.seeseemelk.jtsc.decoders.ntv.NativeTypeInsnDecoder;
import be.seeseemelk.jtsc.decoders.ntv.NativeVarInsnDecoder;
import be.seeseemelk.jtsc.instructions.FieldInstruction;
import be.seeseemelk.jtsc.instructions.Instruction;
import be.seeseemelk.jtsc.instructions.IntInstruction;
import be.seeseemelk.jtsc.instructions.InvokeDynamicInstruction;
import be.seeseemelk.jtsc.instructions.LoadConstantInstruction;
import be.seeseemelk.jtsc.instructions.MethodCallInstruction;
import be.seeseemelk.jtsc.instructions.TypeInstruction;
import be.seeseemelk.jtsc.instructions.VarInstruction;
import be.seeseemelk.jtsc.instructions.ZeroArgInstruction;
import be.seeseemelk.jtsc.instructions.tree.BasicInstructionNode;
import be.seeseemelk.jtsc.instructions.tree.ConditionalInstructionNode;
import be.seeseemelk.jtsc.instructions.tree.InstructionNode;
import be.seeseemelk.jtsc.instructions.tree.TerminalInstructionNode;

public class TreeWriter
{
	private InstructionTree tree;
	private SourceWriter writer;
	private MethodState state;
	private BaseWriter baseWriter;
	
	public TreeWriter(InstructionTree tree, SourceWriter writer, MethodDescriptor descriptor)
	{
		this.tree = tree;
		this.writer = writer;
		state = new MethodState(writer);
		state.setMethodStatic(descriptor.isStatic());
		baseWriter = new BaseWriter(writer, descriptor);
	}
	
	public InstructionTree getTree()
	{
		return tree;
	}
	
	public SourceWriter getWriter()
	{
		return writer;
	}
	
	public void write()
	{
		baseWriter.writePrelude();
		InstructionNode node = tree.getStart();
		if (node != null)
			writeNode(node);
		baseWriter.writePostlude();
	}
	
	private void writeNode(InstructionNode node)
	{
		if (node instanceof BasicInstructionNode)
			writeBasicInstructionNode((BasicInstructionNode) node);
		else if (node instanceof TerminalInstructionNode)
			writeTerminalInstructionNode((TerminalInstructionNode) node);
		else if (node instanceof ConditionalInstructionNode)
			writeConditionalInstructionNode((ConditionalInstructionNode) node);
		else
			throw new RuntimeException("Unsupported node type: " + node.getClass().getSimpleName());
	}
	
	private void writeBasicInstructionNode(BasicInstructionNode node)
	{
		writeInstruction(node.getInstruction());
		if (node.hasNext())
			writeNode(node.next());
	}
	
	private void writeTerminalInstructionNode(TerminalInstructionNode node)
	{
		writeInstruction(node.getInstruction());
	}
	
	private void writeConditionalInstructionNode(ConditionalInstructionNode node)
	{
		var instr = node.getInstruction();
		writer.writeUnsafe("if (");
		NativeJumpInsnDecoder.visit(state, instr.getOpcode());
		writer.writelnUnsafe(") {");
		writer.indent();
		writeNode(node.getTrueBranch());
		writer.undent();
		writer.writelnUnsafe("} else {");
		writer.indent();
		writeNode(node.getFalseBranch());
		writer.undent();
		writer.writelnUnsafe("}");
	}
	
	private void writeInstruction(Instruction instruction)
	{
		if (instruction instanceof ZeroArgInstruction)
			writeZeroArgInstruction((ZeroArgInstruction) instruction);
		else if (instruction instanceof MethodCallInstruction)
			writeMethodCallInstruction((MethodCallInstruction) instruction);
		else if (instruction instanceof VarInstruction)
			writeVarInstruction((VarInstruction) instruction);
		else if (instruction instanceof FieldInstruction)
			writeFieldInstruction((FieldInstruction) instruction);
		else if (instruction instanceof LoadConstantInstruction)
			writeLoadConstantInstruction((LoadConstantInstruction) instruction);
		else if (instruction instanceof InvokeDynamicInstruction)
			writeInvokeDynamicInstruction((InvokeDynamicInstruction) instruction);
		else if (instruction instanceof TypeInstruction)
			writeTypeInstruction((TypeInstruction) instruction);
		else if (instruction instanceof IntInstruction)
			writeIntInstruction((IntInstruction) instruction);
		else
			throw new RuntimeException(
					"Unsupported instruction type: "
					+ instruction.getClass().getSimpleName());
	}
	
	private void writeZeroArgInstruction(ZeroArgInstruction instruction)
	{
		NativeInsnDecoder.visit(state, instruction.getOpcode());
	}
	
	private void writeMethodCallInstruction(MethodCallInstruction instruction)
	{
		NativeMethodInsnDecoder.visit(
				state,
				instruction.getOpcode(),
				instruction.getOwner(),
				instruction.getName(),
				instruction.getDescriptor(),
				instruction.isInterface());
	}
	
	private void writeVarInstruction(VarInstruction instruction)
	{
		NativeVarInsnDecoder.visit(state, instruction.getOpcode(), instruction.getVar());
	}

	private void writeFieldInstruction(FieldInstruction instruction)
	{
		NativeFieldInsnDecoder.visit(
				state,
				instruction.getOpcode(),
				instruction.getOwner(),
				instruction.getName(),
				instruction.getDescriptor());
	}
	
	private void writeLoadConstantInstruction(LoadConstantInstruction instruction)
	{
		NativeLdcInsnDecoder.visit(state, instruction.getValue());
	}
	
	private void writeInvokeDynamicInstruction(InvokeDynamicInstruction instruction)
	{
		NativeMethodInsnDecoder.visitDynamic(
				state,
				instruction.getName(),
				instruction.getDescriptor(),
				instruction.getBootstrapMethodHandle(),
				instruction.getBootstrapMethodArguments()
		);
	}
	
	private void writeTypeInstruction(TypeInstruction instruction)
	{
		NativeTypeInsnDecoder.visit(
				state,
				instruction.getOpcode(),
				instruction.getType()
		);
	}
	
	private void writeIntInstruction(IntInstruction instruction)
	{
		NativeIntInsnDecoder.visit(state, instruction);
	}
}





















