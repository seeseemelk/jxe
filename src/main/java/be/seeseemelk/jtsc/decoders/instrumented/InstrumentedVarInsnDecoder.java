package be.seeseemelk.jtsc.decoders.instrumented;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.MethodDescriptor;
import be.seeseemelk.jtsc.recompiler.SourceWriter;

public class InstrumentedVarInsnDecoder
{
	public static void visit(
			MethodDescriptor descriptor,
			SourceWriter writer,
			int opcode,
			int var
	)
	{
		try
		{
			String varName = getVarName(descriptor, var);
			
			switch (opcode)
			{
				case Opcodes.ALOAD:
					writer.writelnUnsafe("aload(vars, ", varName, ");");
					break;
				case Opcodes.ILOAD:
					writer.writelnUnsafe("iload(vars, ", varName, ");");
					break;
					/*
				case Opcodes.DLOAD:
				case Opcodes.FLOAD:
				case Opcodes.LLOAD:
					visitLoad(state, var);
					break;
				case Opcodes.ISTORE:
				case Opcodes.LSTORE:
				case Opcodes.FSTORE:
				case Opcodes.DSTORE:
				case Opcodes.ASTORE:
					visitStore(state, var);
					break;
				case Opcodes.RET:
				//*/
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
	
	private static String getVarName(MethodDescriptor descriptor, int var)
	{
		if (descriptor.isStatic())
		{
			return "var" + var;
		}
		else
		{
			if (var == 0)
				return "this";
			else
				return "var" + (var - 1);
		}
	}
}
