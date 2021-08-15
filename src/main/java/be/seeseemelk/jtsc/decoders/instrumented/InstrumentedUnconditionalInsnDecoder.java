package be.seeseemelk.jtsc.decoders.instrumented;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.SourceWriter;

public class InstrumentedUnconditionalInsnDecoder
{
	public static void visit(SourceWriter writer, int opcode, int index)
	{
		switch (opcode)
		{
		case Opcodes.GOTO:
			writer.writelnUnsafe("address = ", index, ";");
		break;
		default:
			throw new RuntimeException(String.format("Unsupported opcode: 0x%02X [index=%d]", opcode, index));
		}
	}
}
