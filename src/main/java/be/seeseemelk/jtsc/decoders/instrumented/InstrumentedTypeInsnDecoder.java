package be.seeseemelk.jtsc.decoders.instrumented;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.SourceWriter;
import be.seeseemelk.jtsc.recompiler.Utils;

public class InstrumentedTypeInsnDecoder
{
	public static void visit(SourceWriter writer, int opcode, String type)
	{
		String dType = Utils.typeToName(type);
		switch (opcode)
		{
		case Opcodes.NEW:
			writer.writelnUnsafe("vars ~= JavaVar.ofObject(new ", type, ");");
		break;
		case Opcodes.CHECKCAST:
			writer.writelnUnsafe("if (vars[$-1].asObject !is null && (cast(", dType, ") vars[$-1].asObject) is null)");
			writer.indent();
			writer.writelnUnsafe("throw new __JavaException(new ClassCastException());");
			writer.undent();
		break;
		case Opcodes.INSTANCEOF:
			writer.writelnUnsafe("if (vars[$-1].asObject is null) ");
			writer.indent();
			writer.writelnUnsafe("vars ~= JavaVar.ofInt(0);");
			writer.undent();
			writer.writelnUnsafe("else if (cast(", dType, ") vars[$-1].asObject is null)");
			writer.indent();
			writer.writelnUnsafe("vars ~= JavaVar.ofInt(0);");
			writer.undent();
			writer.writelnUnsafe("else");
			writer.indent();
			writer.writelnUnsafe("vars ~= JavaVar.ofInt(1);");
			writer.undent();
		break;
		default:
			throw new RuntimeException(String.format("Unknown opcode 0x%02X (type=%s)", opcode, type));
		}
	}
}
