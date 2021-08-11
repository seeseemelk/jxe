package be.seeseemelk.jtsc.recompiler;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.decoders.instrumented.InstrumentedFieldInsnDecoder;
import be.seeseemelk.jtsc.decoders.instrumented.InstrumentedInsnDecoder;
import be.seeseemelk.jtsc.decoders.instrumented.InstrumentedMethodInsnDecoder;
import be.seeseemelk.jtsc.decoders.instrumented.InstrumentedVarInsnDecoder;
import be.seeseemelk.jtsc.instructions.ConditionalInstruction;
import be.seeseemelk.jtsc.instructions.FieldInstruction;
import be.seeseemelk.jtsc.instructions.Instruction;
import be.seeseemelk.jtsc.instructions.MethodCallInstruction;
import be.seeseemelk.jtsc.instructions.UnconditionalInstruction;
import be.seeseemelk.jtsc.instructions.VarInstruction;
import be.seeseemelk.jtsc.instructions.ZeroArgInstruction;

public final class InstrumentedStreamWriter
{
	private final SourceWriter writer;
	private final MethodDescriptor descriptor;
	private final InstructionStream stream;
	
	private InstrumentedStreamWriter(
		SourceWriter writer,
		MethodDescriptor descriptor,
		InstructionStream stream
	)
	{
		this.writer = writer;
		this.descriptor = descriptor;
		this.stream = stream;
	}

	public static void write(InstructionStream stream, MethodDescriptor descriptor, SourceWriter writer)
	{
		var streamWriter = new InstrumentedStreamWriter(writer, descriptor, stream);
		var baseWriter = new BaseWriter(writer, descriptor);
		baseWriter.writePrelude();
		streamWriter.writeBody();
		baseWriter.writePostlude();
	}
	
	private void writeBody()
	{
		writer.writelnUnsafe("import java.lang.instrumentation;");
		writer.writelnUnsafe("JavaVar[] vars;");
		writer.writelnUnsafe("size_t address = 0;");
		writer.writelnUnsafe("for (;;) {");
		writer.indent();
		writer.writelnUnsafe("switch (address) {");
		for (int i = 0; i < stream.size(); i++)
		{
			writer.writelnUnsafe("case ", i, ":");
			writer.indent();
			writeInstruction(stream.get(i));
			writer.undent();
		}
		writer.writelnUnsafe("default:");
		writer.indent();
		writer.writelnUnsafe("assert(0, \"Invalid address\");");
		writer.undent();
		writer.writelnUnsafe("}");
		writer.undent();
		writer.writelnUnsafe("}");
	}

	private void writeInstruction(Instruction instruction)
	{
		if (instruction instanceof VarInstruction)
		{
			VarInstruction instr = (VarInstruction) instruction;
			InstrumentedVarInsnDecoder.visit(
					descriptor,
					writer,
					instr.getOpcode(),
					instr.getVar()
			);
		}
		else if (instruction instanceof MethodCallInstruction)
		{
			MethodCallInstruction instr = (MethodCallInstruction) instruction;
			InstrumentedMethodInsnDecoder.visit(
					descriptor,
					writer,
					instr.getOpcode(),
					instr.getOwner(),
					instr.getName(),
					instr.getDescriptor(),
					instr.isInterface()
			);
		}
		else if (instruction instanceof ZeroArgInstruction)
		{
			ZeroArgInstruction instr = (ZeroArgInstruction) instruction;
			InstrumentedInsnDecoder.visit(writer, instr.getOpcode());
		}
		else if (instruction instanceof ConditionalInstruction)
		{
			writer.writelnUnsafe("// Conditional");
		}
		else if (instruction instanceof FieldInstruction)
		{
			FieldInstruction instr = (FieldInstruction) instruction;
			InstrumentedFieldInsnDecoder.visit(
					writer,
					instr.getOpcode(),
					instr.getOwner(),
					instr.getName(),
					instr.getDescriptor()
			);
		}
		else
			throw new RuntimeException("Unknown instruction of type " + instruction.getClass().getSimpleName());
		
		if (instruction instanceof UnconditionalInstruction)
		{
			var uncond = (UnconditionalInstruction) instruction;
			var index = stream.getIndex(uncond.getTarget());
			writer.writelnUnsafe("address = ", index, ";");
			writer.writelnUnsafe("break;");
		}
		else if (!isReturn(instruction))
		{
			writer.writelnUnsafe("address++;");
			writer.writelnUnsafe("break;");
		}
	}
	
	private boolean isReturn(Instruction instruction)
	{
		if (instruction instanceof ZeroArgInstruction)
		{
			var instr = (ZeroArgInstruction) instruction;
			return instr.getOpcode() == Opcodes.RETURN;
		}
		else
		{
			return false;
		}
	}
}
