package be.seeseemelk.jtsc.decoders.instrumented;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.InstrumentedMethod;
import be.seeseemelk.jtsc.recompiler.SourceWriter;

public class InstrumentedVarInsnDecoder
{
	public static void visit(
			InstrumentedMethod method,
			SourceWriter writer,
			int opcode,
			int var
	)
	{
		try
		{
			switch (opcode)
			{
				case Opcodes.ALOAD:
					writer.writelnUnsafe("vars ~= JavaVar.ofObject(", method.accessVar(var, "asObject"), ");");
				break;
				case Opcodes.ILOAD:
					writer.writelnUnsafe("vars ~= JavaVar.ofInt(", method.accessVar(var, "asInt"), ");");
				break;
//				case Opcodes.DLOAD:
//				case Opcodes.FLOAD:
//				case Opcodes.LLOAD:
//					visitLoad(state, var);
//					break;
				case Opcodes.ISTORE:
				case Opcodes.LSTORE:
				case Opcodes.FSTORE:
				case Opcodes.DSTORE:
				case Opcodes.ASTORE:
					writer.writelnUnsafe(method.getVar(var), " = vars[$-1];");
				break;
				default:
					throw new UnsupportedOperationException("Unknown opcode: " + opcode + ", " + var);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(String.format(
					"Exception occured while processing 0x%X[var=%d]",
					opcode, var),
					e);
		}
	}
}
