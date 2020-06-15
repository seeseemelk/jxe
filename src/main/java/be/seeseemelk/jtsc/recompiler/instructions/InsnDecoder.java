package be.seeseemelk.jtsc.recompiler.instructions;

import java.io.IOException;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.MethodState;

public final class InsnDecoder
{
	private InsnDecoder()
	{
	}
	
	private static void visitReturn(MethodState state) throws IOException
	{
		writeStackAsStatement(state);
		state.getWriter().writeln("return;");
	}
	
	private static void visitNReturn(MethodState state) throws IOException
	{
		writeStackAsStatement(state);
		state.getWriter().writeln("return ", state.popFromStack());
	}
	
	/**
	 * Writes the entirety of the stack as a statement. This *should not* generate
	 * any output as the stack should be empty. However, any incorrectly decompiled
	 * instructions can cause the stack to still contain some data. This method will
	 * make these errors easily visible.
	 * @throws IOException 
	 */
	private static final void writeStackAsStatement(MethodState state) throws IOException
	{
		if (state.isStackEmpty())
			return;
		
		state.getWriter().writeln("// Start of stack dump");
		while (!state.isStackEmpty())
		{
			String statement = state.popFromStack();
			state.getWriter().writeln(statement, ";");
		}
		state.getWriter().writeln("// End of stack dump");
	}

	private static void visitDup(MethodState state) throws IOException
	{
		var expression = state.popFromStack();
		state.pushToStack(expression);
		state.pushToStack(expression);
	}
	
	private static void visitAnd(MethodState state)
	{
		doBinaryOperation(state, "&");
	}

	private static void visitMul(MethodState state)
	{
		doBinaryOperation(state, "*");
	}

	private static void visitSub(MethodState state)
	{
		doBinaryOperation(state, "-");
	}

	private static void visitAdd(MethodState state)
	{
		doBinaryOperation(state, "+");
	}

	/**
	 * Performs a binary operation, taking the operands from the stack and
	 * storing the result back onto the stack.
	 * @param state
	 * @param operation The symbol for the operation to perform.
	 */
	private static void doBinaryOperation(MethodState state, String operation)
	{
		var value2 = state.popFromStack();
		var value1 = state.popFromStack();
		state.pushToStack(value1 + ' ' + operation + ' ' + value2);
	}

	private static void visitNeg(MethodState state)
	{
		var expression = state.popFromStack();
		state.pushToStack("-" + expression);
	}

	private static void visitD2F(MethodState state)
	{
		doCast(state, "float");
	}
	
	private static void visitF2D(MethodState state)
	{
		doCast(state, "double");
	}

	/**
	 * Performs a type cast.
	 * @param state
	 * @param target The target type of the cast.
	 */
	private static void doCast(MethodState state, String target)
	{
		state.pushToStack("(cast(" + target + ")" + state.popFromStack() + ")");
	}

	private static void visitPop(MethodState state)
	{
		state.popFromStack();
	}

	private static void visitIConst(MethodState state, int value)
	{
		state.pushToStack(Integer.toString(value));
	}

	private static void visitDConst(MethodState state, double value)
	{
		state.pushToStack(Double.toString(value));
	}

	private static void visitAAStore(MethodState state) throws IOException
	{
		var value = state.popFromStack();
		var index = state.popFromStack();
		var array = state.popFromStack();
		state.getWriter().writeln(array, "[", index, "] = ", value, ";");
	}

	private static void visitAALoad(MethodState state)
	{
		var index = state.popFromStack();
		var array = state.popFromStack();
		state.pushToStack(array + "[" + index + "]");
	}

	private static void visitArrayLength(MethodState state)
	{
		var array = state.popFromStack();
		state.pushToStack(array + ".length");
	}

	private static void visitAConstNull(MethodState state)
	{
		state.pushToStack("null");
	}

	private static void visitAThrow(MethodState state) throws IOException
	{
		var expression = state.popFromStack();
		state.getWriter().writeln("throw " + expression);
	}

	public static void visitInsn(MethodState state, int opcode)
	{
		try
		{
			switch (opcode)
			{
				case Opcodes.RETURN:
					visitReturn(state);
					break;
				case Opcodes.IRETURN:
				case Opcodes.LRETURN:
				case Opcodes.FRETURN:
				case Opcodes.DRETURN:
				case Opcodes.ARETURN:
					visitNReturn(state);
					break;
				case Opcodes.DUP:
					visitDup(state);
					break;
				case Opcodes.ATHROW:
					visitAThrow(state);
					break;
				case Opcodes.ACONST_NULL:
					visitAConstNull(state);
					break;
				case Opcodes.ARRAYLENGTH:
					visitArrayLength(state);
					break;
				case Opcodes.AALOAD:
					visitAALoad(state);
					break;
				case Opcodes.AASTORE:
					visitAAStore(state);
					break;
				case Opcodes.DCONST_0:
					visitDConst(state, 0.0);
					break;
				case Opcodes.ICONST_0:
					visitIConst(state, 0);
					break;
				case Opcodes.ICONST_1:
					visitIConst(state, 1);
					break;
				case Opcodes.ICONST_2:
					visitIConst(state, 2);
					break;
				case Opcodes.ICONST_3:
					visitIConst(state, 3);
					break;
				case Opcodes.ICONST_4:
					visitIConst(state, 4);
					break;
				case Opcodes.ICONST_5:
					visitIConst(state, 5);
					break;
				case Opcodes.POP:
					visitPop(state);
					break;
				case Opcodes.IADD:
					visitAdd(state);
					break;
				case Opcodes.DSUB:
					visitSub(state);
					break;
				case Opcodes.FMUL:
					visitMul(state);
					break;
				case Opcodes.F2D:
					visitF2D(state);
					break;
				case Opcodes.D2F:
					visitD2F(state);
					break;
				case Opcodes.FNEG:
					visitNeg(state);
					break;
				case Opcodes.IAND:
					visitAnd(state);
					break;
				default:
					throw new UnsupportedOperationException(String.format("Unknown method: 0x%02X", opcode));
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
