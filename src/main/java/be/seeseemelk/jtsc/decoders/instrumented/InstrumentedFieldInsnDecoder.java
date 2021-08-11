package be.seeseemelk.jtsc.decoders.instrumented;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.SourceWriter;
import be.seeseemelk.jtsc.recompiler.Utils;

public final class InstrumentedFieldInsnDecoder
{
	private InstrumentedFieldInsnDecoder() {}

	private static void visitPutStatic(SourceWriter writer, String owner, String name, String descriptor)
	{
		var type = Utils.typeToName(descriptor);
		writer.writelnUnsafe(owner + "." + name + " = cast(" + type + ") vars.popObject();");
	}

	public static void visit(SourceWriter writer, int opcode, String owner, String name, String descriptor)
	{
		try
		{
			owner = Utils.getClassName(owner);
			name = Utils.identifierToD(name);
			switch (opcode)
			{
				/*case Opcodes.GETSTATIC:
					visitGetStatic(state, owner, name);
					break;
				case Opcodes.GETFIELD:
					visitGetField(state, name);
					break;*/
				case Opcodes.PUTSTATIC:
					visitPutStatic(writer, owner, name, descriptor);
					break;
				/*case Opcodes.PUTFIELD:
					visitPutField(state, name);
					break;*/
				default:
					throw new UnsupportedOperationException("Unknown field instruction " + opcode + ", "
							+ owner + ", " + name + ", " + descriptor);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(String.format(
					"Exception occured while processing 0x%X[owner=%s,name=%s,descriptor=%s]",
					opcode, owner, name, descriptor),
					e);
		}
	}
}
