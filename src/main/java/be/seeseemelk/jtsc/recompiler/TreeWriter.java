package be.seeseemelk.jtsc.recompiler;

import java.util.ArrayList;
import java.util.List;

import com.google.errorprone.annotations.Var;

import be.seeseemelk.jtsc.decoders.FieldInsnDecoder;
import be.seeseemelk.jtsc.decoders.InsnDecoder;
import be.seeseemelk.jtsc.decoders.JumpInsnDecoder;
import be.seeseemelk.jtsc.decoders.LdcInsnDecoder;
import be.seeseemelk.jtsc.decoders.MethodInsnDecoder;
import be.seeseemelk.jtsc.decoders.VarInsnDecoder;
import be.seeseemelk.jtsc.instructions.FieldInstruction;
import be.seeseemelk.jtsc.instructions.Instruction;
import be.seeseemelk.jtsc.instructions.InvokeDynamicInstruction;
import be.seeseemelk.jtsc.instructions.LoadConstantInstruction;
import be.seeseemelk.jtsc.instructions.MethodCallInstruction;
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
	private MethodDescriptor descriptor;

	public TreeWriter(InstructionTree tree, SourceWriter writer, MethodDescriptor descriptor)
	{
		this.tree = tree;
		this.writer = writer;
		this.descriptor = descriptor;
		state = new MethodState(writer);
		state.setMethodStatic(descriptor.isStatic());
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
		writePrelude();
		InstructionNode node = tree.getStart();
		if (node != null)
			writeNode(node);
		writePostlude();
	}
	
	private void writePrelude()
	{
		List<String> keywords = new ArrayList<>();
		
		if (!descriptor.isStaticInitializer())
		{
			switch (descriptor.getVisibility())
			{
				case PRIVATE:
					keywords.add("private ");
					break;
				case PACKAGE:
					System.err.println("Warning: PACKAGE visibility is not supported, using PUBLIC instead");
				case PUBLIC:
					keywords.add("public ");
					break;
				case PROTECTED:
					keywords.add("protected ");
					break;
			}
		}
		
		if (descriptor.isStatic() || descriptor.isStaticInitializer())
			keywords.add("static ");
		
		if (descriptor.isConstructor() || descriptor.isStaticInitializer())
			keywords.add("this");
		else
		{
			keywords.add(descriptor.getReturnType());
			keywords.add(" ");
			keywords.add(descriptor.getName());
		}
		
		keywords.add("(");
		
		int argCount = 0;
		// Non-static functions have a 'this' parameter at index 0.
		int offset = descriptor.isStatic() ? 0 : 1;
		for (var arg : descriptor.getArguments())
		{
			keywords.add(Utils.getClassName(arg));
			keywords.add(" ");
			keywords.add(state.getVariableName(argCount + offset));
			argCount++;
			if (argCount < descriptor.getArguments().size())
				keywords.add(", ");
		}
		
		keywords.add(") ");
		keywords.add("{");
		
		writer.writelnUnsafe(String.join("", keywords));
		writer.indent();
	}
	
	private void writePostlude()
	{
		writer.undent();
		writer.writelnUnsafe("}");
		writer.writelnUnsafe();
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
		JumpInsnDecoder.visit(state, instr.getOpcode());
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
		else
			throw new RuntimeException(
					"Unsupported instruction type: "
					+ instruction.getClass().getSimpleName());
	}
	
	private void writeZeroArgInstruction(ZeroArgInstruction instruction)
	{
		InsnDecoder.visit(state, instruction.getOpcode());
	}
	
	private void writeMethodCallInstruction(MethodCallInstruction instruction)
	{
		MethodInsnDecoder.visit(
				state,
				instruction.getOpcode(),
				instruction.getOwner(),
				instruction.getName(),
				instruction.getDescriptor(),
				instruction.isInterface());
	}
	
	private void writeVarInstruction(VarInstruction instruction)
	{
		VarInsnDecoder.visit(state, instruction.getOpcode(), instruction.getVar());
	}

	private void writeFieldInstruction(FieldInstruction instruction)
	{
		FieldInsnDecoder.visit(
				state,
				instruction.getOpcode(),
				instruction.getOwner(),
				instruction.getName(),
				instruction.getDescriptor());
	}
	
	private void writeLoadConstantInstruction(LoadConstantInstruction instruction)
	{
		LdcInsnDecoder.visit(state, instruction.getValue());
	}
	
	private void writeInvokeDynamicInstruction(InvokeDynamicInstruction instruction)
	{
		MethodInsnDecoder.visitDynamic(
				state,
				instruction.getName(),
				instruction.getDescriptor(),
				instruction.getBootstrapMethodHandle(),
				instruction.getBootstrapMethodArguments()
		);
	}
}





















