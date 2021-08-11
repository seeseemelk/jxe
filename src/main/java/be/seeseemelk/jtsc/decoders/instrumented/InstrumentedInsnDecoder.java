package be.seeseemelk.jtsc.decoders.instrumented;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.SourceWriter;

public final class InstrumentedInsnDecoder
{
	private InstrumentedInsnDecoder()
	{
	}

	public static void visit(SourceWriter writer, int opcode)
	{
		try
		{
			switch (opcode)
			{
				/*case Opcodes.RETURN:
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
				case Opcodes.I2F:
					visitPrimitiveCast(state, "float");
					break;*/
				case Opcodes.RETURN:
					writer.writelnUnsafe("return;");
					break;
				default:
					throw new UnsupportedOperationException(String.format("Unknown method: 0x%02X", opcode));
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(String.format("Exception occured while processing 0x%X", opcode), e);
		}
	}
}
