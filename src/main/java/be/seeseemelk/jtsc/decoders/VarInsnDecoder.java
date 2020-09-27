package be.seeseemelk.jtsc.decoders;

import java.io.IOException;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.MethodState;

public final class VarInsnDecoder
{
	private VarInsnDecoder () {}
	
	private static void visitALoad(MethodState state, int var)
	{
		if (var == 0 && !state.isMethodStatic())
			state.pushToStack("this");
		else
			state.pushToStack(state.getVariableName(var));
	}
	
	private static void visitLoad(MethodState state, int var)
	{
		state.pushToStack(state.getVariableName(var));
	}
	
	private static void visitStore(MethodState state, int var) throws IOException
	{
		state.getWriter().writeln(state.getVariableName(var), " = ", state.popFromStack(), ";");
	}
	
	public static void visit(MethodState state, int opcode, int var)
	{
		try
		{
			switch (opcode)
			{
				case Opcodes.ALOAD:
					visitALoad(state, var);
					break;
				case Opcodes.ILOAD:
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
