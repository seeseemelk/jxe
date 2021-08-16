package be.seeseemelk.jtsc.recompiler;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.decoders.instrumented.InstrumentedConditionalInsnDecoder;
import be.seeseemelk.jtsc.decoders.instrumented.InstrumentedFieldInsnDecoder;
import be.seeseemelk.jtsc.decoders.instrumented.InstrumentedInsnDecoder;
import be.seeseemelk.jtsc.decoders.instrumented.InstrumentedIntInsnDecoder;
import be.seeseemelk.jtsc.decoders.instrumented.InstrumentedLoadConstantInsnDecoder;
import be.seeseemelk.jtsc.decoders.instrumented.InstrumentedMethodInsnDecoder;
import be.seeseemelk.jtsc.decoders.instrumented.InstrumentedTypeInsnDecoder;
import be.seeseemelk.jtsc.decoders.instrumented.InstrumentedUnconditionalInsnDecoder;
import be.seeseemelk.jtsc.decoders.instrumented.InstrumentedVarInsnDecoder;
import be.seeseemelk.jtsc.decoders.instrumented.IntegerIncrementInsnDecoder;
import be.seeseemelk.jtsc.instructions.ConditionalInstruction;
import be.seeseemelk.jtsc.instructions.FieldInstruction;
import be.seeseemelk.jtsc.instructions.Instruction;
import be.seeseemelk.jtsc.instructions.IntInstruction;
import be.seeseemelk.jtsc.instructions.IntegerIncrementInstruction;
import be.seeseemelk.jtsc.instructions.InvokeDynamicInstruction;
import be.seeseemelk.jtsc.instructions.LoadConstantInstruction;
import be.seeseemelk.jtsc.instructions.MethodCallInstruction;
import be.seeseemelk.jtsc.instructions.TypeInstruction;
import be.seeseemelk.jtsc.instructions.UnconditionalInstruction;
import be.seeseemelk.jtsc.instructions.VarInstruction;
import be.seeseemelk.jtsc.instructions.ZeroArgInstruction;

public final class InstrumentedStreamWriter
{
	private final SourceWriter writer;
	private final MethodDescriptor descriptor;
	private final InstructionStream stream;
	private final InstrumentedMethod method;

	private InstrumentedStreamWriter(
		SourceWriter writer,
		MethodDescriptor descriptor,
		InstructionStream stream
	)
	{
		this.writer = writer;
		this.descriptor = descriptor;
		this.stream = stream;
		this.method = new InstrumentedMethod(descriptor, stream);
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
		writer.writelnUnsafe("import java.lang.instrumentation : JavaVar;");
		for (int i = 0; i < stream.getLocals() - 1; i++)
		{
			writer.writelnUnsafe("JavaVar var", i, ";");
		}
		writer.writelnUnsafe("JavaVar[] vars;");
		writer.writelnUnsafe("size_t address = 0;");
		writer.writelnUnsafe("for (;;) {");
		writer.indent();
		writer.writelnUnsafe("switch (address) {");
		for (int i = 0; i < stream.size(); i++)
		{
			writer.writelnUnsafe("case ", i, ":");
			writer.indent();
			writeInstruction(stream, i);
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

	private void writeInstruction(InstructionStream stream, int address)
	{
		Instruction instruction = stream.get(address);
		int next = address + 1;

		if (instruction instanceof VarInstruction)
		{
			VarInstruction instr = (VarInstruction) instruction;
			InstrumentedVarInsnDecoder.visit(
					method,
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
			InstrumentedInsnDecoder.visit(writer, instr.getOpcode(), descriptor);
		}
		else if (instruction instanceof ConditionalInstruction)
		{
			ConditionalInstruction instr = (ConditionalInstruction) instruction;
			InstrumentedConditionalInsnDecoder.visit(
					writer,
					instr.getOpcode(),
					stream.getIndex(instr.getTarget()),
					next
			);
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
		else if (instruction instanceof InvokeDynamicInstruction)
		{
			InvokeDynamicInstruction instr = (InvokeDynamicInstruction) instruction;
			InstrumentedMethodInsnDecoder.visitDynamic(
					writer,
					instr.getName(),
					instr.getDescriptor(),
					instr.getBootstrapMethodHandle(),
					instr.getBootstrapMethodArguments()
			);
		}
		else if (instruction instanceof LoadConstantInstruction instr)
		{
			InstrumentedLoadConstantInsnDecoder.visit(
					writer,
					instr.getValue()
			);
		}
		else if (instruction instanceof TypeInstruction instr)
		{
			InstrumentedTypeInsnDecoder.visit(
					writer,
					instr.getOpcode(),
					instr.getType()
			);
		}
		else if (instruction instanceof IntegerIncrementInstruction instr)
		{
			IntegerIncrementInsnDecoder.visit(
					method,
					writer,
					instr.getVar(),
					instr.getCount()
			);
		}
		else if (instruction instanceof UnconditionalInstruction instr)
		{
			InstrumentedUnconditionalInsnDecoder.visit(
					writer,
					instr.getOpcode(),
					stream.getIndex(instr.getTarget())
			);
		}
		else if (instruction instanceof IntInstruction instr)
		{
			InstrumentedIntInsnDecoder.visit(
					writer,
					instr.getOpcode(),
					instr.getValue()
			);
		}
		else
			throw new RuntimeException("Unknown instruction of type " + instruction.getClass().getSimpleName());

		if (instruction instanceof UnconditionalInstruction)
		{
			var uncond = (UnconditionalInstruction) instruction;
			next = stream.getIndex(uncond.getTarget());
		}

		boolean isReturn = isReturn(instruction);
		boolean isConditional = isConditional(instruction);
		if (!isReturn && !isConditional)
		{
			writer.writelnUnsafe("address = ", next, ";");
		}
		if (!isReturn)
		{
			writer.writelnUnsafe("break;");
		}
	}

	private boolean isReturn(Instruction instruction)
	{
		if (instruction instanceof ZeroArgInstruction)
		{
			var instr = (ZeroArgInstruction) instruction;
			var opcode = instr.getOpcode();
			return opcode == Opcodes.RETURN
			    || opcode == Opcodes.ARETURN
			    || opcode == Opcodes.DRETURN
			    || opcode == Opcodes.FRETURN
			    || opcode == Opcodes.IRETURN
			    || opcode == Opcodes.LRETURN
			;
		}
		{
			return false;
		}
	}

	private boolean isConditional(Instruction instruction)
	{
		return instruction instanceof ConditionalInstruction;
	}
}
