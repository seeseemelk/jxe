package be.seeseemelk.jtsc.recompiler.instructions;

import java.io.IOException;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.MethodState;

public final class JumpInsnDecoder
{
	private JumpInsnDecoder() {}
	
	private static void visitIfICmp(MethodState state, Label label, String operator) throws IOException
	{
		var valueA = state.popFromStack();
		var valueB = state.popFromStack();
		var target = state.getLabelName(label);
		state.getWriter().writeln("if (", valueA, " ", operator, " ", valueB , ") goto ", target, ";");
	}
	
	private static void visitIfxx(MethodState state, Label label, String operator) throws IOException
	{
		var value = state.popFromStack();
		var target = state.getLabelName(label);
		state.getWriter().writeln("if (", value, " ", operator, " 0) goto ", target, ";");
	}
	
	private static void visitGoto(MethodState state, Label label) throws IOException
	{
		var target = state.getLabelName(label);
		state.getWriter().writeln("goto ", target, ";");
	}
	
	public static void visit(MethodState state, int opcode, Label label)
	{
		try
		{
			switch (opcode)
			{
				case Opcodes.IFEQ:
					visitIfxx(state, label, "==");
					break;
				case Opcodes.IFNE:
					visitIfxx(state, label, "!=");
					break;
				case Opcodes.IFLT:
					visitIfxx(state, label, "<");
					break;
				case Opcodes.IFLE:
					visitIfxx(state, label, "<=");
					break;
				case Opcodes.IFGT:
					visitIfxx(state, label, ">");
					break;
				case Opcodes.IFGE:
					visitIfxx(state, label, ">=");
					break;
				case Opcodes.IF_ICMPEQ:
					visitIfICmp(state, label, "==");
					break;
				case Opcodes.IF_ICMPNE:
					visitIfICmp(state, label, "!=");
					break;
				case Opcodes.IF_ICMPLT:
					visitIfICmp(state, label, "<");
					break;
				case Opcodes.IF_ICMPLE:
					visitIfICmp(state, label, "<=");
					break;
				case Opcodes.IF_ICMPGT:
					visitIfICmp(state, label, ">");
					break;
				case Opcodes.IF_ICMPGE:
					visitIfICmp(state, label, ">=");
					break;
				case Opcodes.GOTO:
					visitGoto(state, label);
					break;
				default:
					throw new UnsupportedOperationException("Unknown opcode: " + opcode + ", " + state.getLabelName(label));
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(String.format(
					"Exception occured while processing 0x%X[label=%s]",
					opcode, state.getLabelName(label)),
					e);
		}
	}
}
