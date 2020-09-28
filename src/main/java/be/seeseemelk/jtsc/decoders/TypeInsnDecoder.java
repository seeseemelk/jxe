package be.seeseemelk.jtsc.decoders;

import java.io.IOException;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.MethodState;
import be.seeseemelk.jtsc.recompiler.Utils;

public final class TypeInsnDecoder
{
	private TypeInsnDecoder() {}
	
	private static void visitNew(MethodState state, String type) throws IOException
	{
		type = Utils.identifierToD(type);
		String var = state.createLocalVariable();
		state.getWriter().writeln(type + " " + var + ";");
		state.pushToStack(var);
	}
	
	private static void visitCheckCast(MethodState state, String type)
	{
		var expr = state.popFromStack();
		var operation = String.format("checkedCast!(%s)(%s)", type, expr);
		state.pushToStack(operation);
	}
	
	private static void visitInstanceof(MethodState state, String type)
	{
		var expr = state.popFromStack();
		var operation = String.format("(cast(%s) (%s) !is null)", type, expr);
		state.pushToStack(operation);
	}
	
	private static void visitANewArray(MethodState state, String type) throws IOException
	{
		String local = state.createLocalVariable();
		state.getWriter().writeln("auto ", local, " = new ", type, "[", state.popFromStack(), "];");
		state.pushToStack(local);
	}
	
	public static void visit(MethodState state, int opcode, String type)
	{
		try
		{
			switch (opcode)
			{
				case Opcodes.NEW:
					visitNew(state, type);
					break;
				case Opcodes.CHECKCAST:
					visitCheckCast(state, type);
					break;
				case Opcodes.INSTANCEOF:
					visitInstanceof(state, type);
					break;
				case Opcodes.ANEWARRAY:
					visitANewArray(state, type);
					break;
				default:
					throw new UnsupportedOperationException("Unknown )type: " + opcode + ", " + type);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(String.format(
					"Exception occured while processing 0x%X[type=%s]",
					opcode, type),
					e);
		}
	}
}
