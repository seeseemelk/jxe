package be.seeseemelk.jtsc.decoders.instrumented;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.SourceWriter;

public class InstrumentedIntInsnDecoder
{

	public static void visit(SourceWriter writer, int opcode, int value)
	{
		switch (opcode)
		{
		case Opcodes.BIPUSH:
			writer.writelnUnsafe("vars ~= JavaVar.ofInt(value);");
		break;
		default:
			throw new RuntimeException(String.format(
					"Unknown int instruction 0x%02X[value=%d]", opcode, value
			));
		}
	}

}
