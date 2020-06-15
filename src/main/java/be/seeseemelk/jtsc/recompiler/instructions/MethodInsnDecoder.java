package be.seeseemelk.jtsc.recompiler.instructions;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import be.seeseemelk.jtsc.recompiler.MethodState;
import be.seeseemelk.jtsc.recompiler.Utils;

public final class MethodInsnDecoder
{
	private MethodInsnDecoder() {}
	
	private static void visitInvokeSpecial(MethodState state, int opcode, String owner, String descriptor) throws IOException
	{
		if ((opcode & Opcodes.ACC_SUPER) != 0)
		{
			int argCount = Type.getArgumentTypes(descriptor).length;
			LinkedList<String> keywords = new LinkedList<>();
			LinkedList<String> arguments = new LinkedList<>();
			for (int i = 0; i < argCount; i++)
			{
				arguments.push(state.popFromStack());
			}
			keywords.add("(");
			keywords.add(String.join(", ", arguments));
			keywords.add(")");
			String objectRef = state.popFromStack(); // Pop 'this' reference
			if (objectRef.equals("this"))
			{
				keywords.push("super");
			}
			else
			{
				keywords.push(objectRef + " = new " + Utils.identifierToD(owner));
			}
			keywords.add(";");
			String construction = String.join("", keywords);
			state.getWriter().writeln(construction);
		}
		else
			throw new UnsupportedOperationException("Cannot perform INVOKESPECIAL without ACC_SUPER");
	}
	
	private static void visitInvokeVirtual(MethodState state, String name, String descriptor)
	{
		List<String> arguments = state.popFromStack(Type.getArgumentTypes(descriptor).length);
		String variable = state.popFromStack();
		state.pushToStack(variable + "." + name + "(" + String.join(", ", arguments) + ")");
	}
	
	private static void visitInvokeStatic(MethodState state, String variable, String name, String descriptor)
	{
		List<String> arguments = state.popFromStack(Type.getArgumentTypes(descriptor).length);
		state.pushToStack(Utils.getClassName(variable) + "." + name + "(" + String.join(", ", arguments) + ")");
	}
	
	public static void visit(MethodState state, int opcode, String owner, String name, String descriptor, boolean isInterface)
	{
		try
		{
			switch (opcode)
			{
				case Opcodes.INVOKESPECIAL:
					visitInvokeSpecial(state, opcode, owner, descriptor);
					break;
				case Opcodes.INVOKEVIRTUAL:
				case Opcodes.INVOKEINTERFACE:
					visitInvokeVirtual(state, Utils.identifierToD(name), descriptor);
					break;
				case Opcodes.INVOKESTATIC:
					visitInvokeStatic(state, Utils.identifierToD(owner), Utils.identifierToD(name), descriptor);
					break;
				default:
					throw new UnsupportedOperationException("Unknown method: " + opcode + ", " + owner + ", " + name
							+ ", " + descriptor + ", " + isInterface);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(String.format(
					"Exception occured while processing 0x%X[owner=%s,name=%s,descriptor=%s,isInterface=%s]",
					opcode, owner, name, descriptor, isInterface ? "true" : "false"),
					e);
		}
	}
}
