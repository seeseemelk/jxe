package be.seeseemelk.jtsc.decoders;

import java.io.IOException;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.MethodState;

public final class JumpInsnDecoder
{
	private JumpInsnDecoder() {}
	
	private static void visitIfICmp(MethodState state, String operator) throws IOException
	{
		var valueA = state.popFromStack();
		var valueB = state.popFromStack();
		state.getWriter().write(valueA, " ", operator, " ", valueB);
	}
	
	private static void visitIfxx(MethodState state, String operator) throws IOException
	{
		var value = state.popFromStack();
		state.getWriter().write(value, " ", operator, " 0");
	}
	
	public static void visit(MethodState state, int opcode)
	{
		try
		{
			switch (opcode)
			{
				case Opcodes.IFEQ:
					visitIfxx(state, "==");
					break;
				case Opcodes.IFNE:
					visitIfxx(state, "!=");
					break;
				case Opcodes.IFLT:
					visitIfxx(state, "<");
					break;
				case Opcodes.IFLE:
					visitIfxx(state, "<=");
					break;
				case Opcodes.IFGT:
					visitIfxx(state, ">");
					break;
				case Opcodes.IFGE:
					visitIfxx(state, ">=");
					break;
				case Opcodes.IF_ICMPEQ:
					visitIfICmp(state, "==");
					break;
				case Opcodes.IF_ICMPNE:
					visitIfICmp(state, "!=");
					break;
				case Opcodes.IF_ICMPLT:
					visitIfICmp(state, "<");
					break;
				case Opcodes.IF_ICMPLE:
					visitIfICmp(state, "<=");
					break;
				case Opcodes.IF_ICMPGT:
					visitIfICmp(state, ">");
					break;
				case Opcodes.IF_ICMPGE:
					visitIfICmp(state, ">=");
					break;
				case Opcodes.GOTO:
				default:
					throw new UnsupportedOperationException("Unknown opcode: " + opcode);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(String.format(
					"Exception occured while processing 0x%X",
					opcode),
					e);
		}
	}
}
