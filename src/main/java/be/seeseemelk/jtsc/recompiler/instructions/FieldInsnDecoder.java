package be.seeseemelk.jtsc.recompiler.instructions;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.MethodState;
import be.seeseemelk.jtsc.recompiler.Utils;

public final class FieldInsnDecoder
{
	private FieldInsnDecoder()
	{
	}
	
	private static void visitGetStatic(MethodState state, String owner, String field)
	{
		state.pushToStack(owner + "." + field);
	}
	
	private static void visitGetField(MethodState state, String field)
	{
		var ref = state.popFromStack();
		state.pushToStack(ref + "." + field);
	}
	
	private static void visitPutStatic(MethodState state, String owner, String field) throws IOException
	{
		var value = state.popFromStack();
		state.getWriter().writeln(owner, ".", field, " = ", value, ";");
	}
	
	private static void visitPutField(MethodState state, String field) throws IOException
	{
		var value = state.popFromStack();
		var objectRef = state.popFromStack();
		state.getWriter().writeln(objectRef, ".", field, " = ", value, ";");
	}
	
	public static void visit(MethodState state, int opcode, String owner, String name, String descriptor)
	{
		try
		{
			owner = Utils.getClassName(owner);
			name = Utils.identifierToD(name);
			switch (opcode)
			{
				case Opcodes.GETSTATIC:
					visitGetStatic(state, owner, name);
					break;
				case Opcodes.GETFIELD:
					visitGetField(state, name);
					break;
				case Opcodes.PUTSTATIC:
					visitPutStatic(state, owner, name);
					break;
				case Opcodes.PUTFIELD:
					visitPutField(state, name);
					break;
				default:
					throw new UnsupportedOperationException("Unknown field instruction " + opcode + ", "
							+ owner + ", " + name + ", " + descriptor);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(String.format(
					"Error occured while processing 0x%X[owner=%s,name=%s,descriptor=%s]",
					opcode, owner, name, descriptor),
					e);
		}
	}
}
