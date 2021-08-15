package be.seeseemelk.jtsc.decoders.instrumented;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.SourceWriter;

public class InstrumentedConditionalInsnDecoder
{
	public static void visit(SourceWriter writer, int opcode, int address, int other)
	{
		switch (opcode)
		{
			case Opcodes.IFEQ:
				visitSingleConditional(writer, address, other, "==");
			break;
			case Opcodes.IFNE:
				visitSingleConditional(writer, address, other, "!=");
			break;
			case Opcodes.IFLT:
				visitSingleConditional(writer, address, other, "<");
			break;
			case Opcodes.IFGE:
				visitSingleConditional(writer, address, other, ">=");
			break;
			case Opcodes.IFGT:
				visitSingleConditional(writer, address, other, ">");
			break;
			case Opcodes.IFLE:
				visitSingleConditional(writer, address, other, "<=");
			break;

			case Opcodes.IF_ICMPEQ:
				visitDoubleConditional(writer, address, other, "==");
				break;
			case Opcodes.IF_ICMPNE:
				visitDoubleConditional(writer, address, other, "!=");
				break;
			case Opcodes.IF_ICMPLT:
				visitDoubleConditional(writer, address, other, "<");
				break;
			case Opcodes.IF_ICMPGE:
				visitDoubleConditional(writer, address, other, ">=");
				break;
			case Opcodes.IF_ICMPGT:
				visitDoubleConditional(writer, address, other, ">");
				break;
			case Opcodes.IF_ICMPLE:
				visitDoubleConditional(writer, address, other, "<=");

			default:
				throw new UnsupportedOperationException(
						String.format("Unknown conditional instruction 0x%02X, %d", opcode, address)
				);
		}
	}

	private static void visitSingleConditional(SourceWriter writer, int address, int other, String operator)
	{
		writer.writelnUnsafe("if (vars[$-1].asInt ",operator," 0)");
		writer.indent();
		writer.writelnUnsafe("address = ", address, ";");
		writer.undent();
		writer.writelnUnsafe("else");
		writer.indent();
		writer.writelnUnsafe("address = ", other, ";");
		writer.undent();
		writer.writelnUnsafe("vars ~= vars[0 .. $-1];");
	}

	private static void visitDoubleConditional(SourceWriter writer, int address, int other, String operator)
	{
		writer.writelnUnsafe("if (vars[$-1].asInt ",operator," vars[$-2].asInt)");
		writer.indent();
		writer.writelnUnsafe("address = ", address, ";");
		writer.undent();
		writer.writelnUnsafe("else");
		writer.indent();
		writer.writelnUnsafe("address = ", other, ";");
		writer.undent();
		writer.writelnUnsafe("vars ~= vars[0 .. $-2];");
	}
}
