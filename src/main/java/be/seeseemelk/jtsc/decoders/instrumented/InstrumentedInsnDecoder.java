package be.seeseemelk.jtsc.decoders.instrumented;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.recompiler.MethodDescriptor;
import be.seeseemelk.jtsc.recompiler.SourceWriter;

public final class InstrumentedInsnDecoder
{
	private InstrumentedInsnDecoder()
	{
	}

	public static void visit(SourceWriter writer, int opcode, MethodDescriptor method)
	{
		try
		{
			switch (opcode)
			{
				case Opcodes.RETURN:
					writer.writelnUnsafe("return;");
				break;
				case Opcodes.ARETURN:
					writer.writelnUnsafe("return cast(" + method.getReturnType() + ") vars[$ - 1].asObject;");
				break;
				case Opcodes.IRETURN:
					if (method.getReturnType() == "bool")
						writer.writelnUnsafe("return vars[$-1].asInt != 0;");
					else
						visitReturnPrimitive(writer, "asInt");
				break;
				case Opcodes.FRETURN:
					visitReturnPrimitive(writer, "asFloat");
				break;
//				case Opcodes.IRETURN:
//				case Opcodes.LRETURN:
//				case Opcodes.DRETURN:
//				case Opcodes.ARETURN:
//					visitNReturn(state);
//					break;
//				case Opcodes.DUP:
//					visitDup(state);
//					break;
//				case Opcodes.ATHROW:
//					visitAThrow(state);
//					break;
//				case Opcodes.ACONST_NULL:
//					visitAConstNull(state);
//					break;
//				case Opcodes.ARRAYLENGTH:
//					visitArrayLength(state);
//					break;
//				case Opcodes.AALOAD:
//					visitAALoad(state);
//					break;
//				case Opcodes.AASTORE:
//					visitAAStore(state);
//					break;
//				case Opcodes.DCONST_0:
//					visitDConst(state, 0.0);
//					break;
				case Opcodes.ICONST_0:
					visitIConst(writer, 0);
				break;
				case Opcodes.ICONST_1:
					visitIConst(writer, 1);
				break;
				case Opcodes.ICONST_2:
					visitIConst(writer, 2);
				break;
				case Opcodes.ICONST_3:
					visitIConst(writer, 3);
				break;
				case Opcodes.ICONST_4:
					visitIConst(writer, 4);
				break;
				case Opcodes.ICONST_5:
					visitIConst(writer, 5);
				break;
//				case Opcodes.POP:
//					visitPop(state);
//					break;
				case Opcodes.IADD:
					writer.writelnUnsafe("vars[$-2] = JavaVar.ofInt(vars[$-1].asInt + vars[$-2].asInt);");
					writer.writelnUnsafe("vars = vars[0 .. $-1];");
				break;
//				case Opcodes.DSUB:
//					visitSub(state);
//					break;
//				case Opcodes.FMUL:
//					visitMul(state);
//					break;
//				case Opcodes.F2D:
//					visitF2D(state);
//					break;
//				case Opcodes.D2F:
//					visitD2F(state);
//					break;
//				case Opcodes.FNEG:
//					visitNeg(state);
//					break;
//				case Opcodes.IAND:
//					visitAnd(state);
//					break;
				case Opcodes.I2F:
					visitPrimitiveCast(writer, "ofFloat", "asInt");
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

	private static void visitIConst(SourceWriter writer, int i)
	{
		writer.writelnUnsafe("vars ~= JavaVar.ofInt(", i, ");");
	}

	private static void visitPrimitiveCast(SourceWriter writer, String target, String source)
	{
		writer.writelnUnsafe("vars ~= JavaVar.",target,"(vars[$-1].",source,");");
	}

	private static void visitReturnPrimitive(SourceWriter writer, String source)
	{
		writer.writelnUnsafe("return vars[$-1].", source, ";");
	}
}
