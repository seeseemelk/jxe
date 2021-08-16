package be.seeseemelk.jtsc.decoders.instrumented;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import be.seeseemelk.jtsc.recompiler.SourceWriter;
import be.seeseemelk.jtsc.recompiler.Utils;

public final class InstrumentedFieldInsnDecoder
{
	private InstrumentedFieldInsnDecoder() {}

	private static void visitPutStatic(SourceWriter writer, String owner, String name, String descriptor)
	{
		var type = Utils.typeToName(descriptor);
		writer.writelnUnsafe(owner + "." + name + " = cast(" + type + ") vars[$ - 1].asObject;");
		writer.writelnUnsafe("vars = vars[0 .. $ - 1];");
	}

	public static void visit(SourceWriter writer, int opcode, String owner, String name, String descriptor)
	{
		try
		{
			owner = Utils.getClassName(owner);
			name = Utils.identifierToD(name);
			switch (opcode)
			{
				/*case Opcodes.GETSTATIC:
					visitGetStatic(state, owner, name);
					break;*/
				case Opcodes.PUTSTATIC:
					visitPutStatic(writer, owner, name, descriptor);
				break;
				case Opcodes.GETFIELD:
					visitGetField(writer, owner, name, descriptor);
				break;
				case Opcodes.PUTFIELD:
					visitPutField(writer, owner, name, descriptor);
				break;
				default:
					throw new UnsupportedOperationException("Unknown field instruction " + opcode + ", "
							+ owner + ", " + name + ", " + descriptor);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(String.format(
					"Exception occured while processing 0x%X[owner=%s,name=%s,descriptor=%s]",
					opcode, owner, name, descriptor),
					e);
		}
	}

	private static void visitGetField(SourceWriter writer, String owner, String name, String descriptor)
	{
		int sort = Type.getType(descriptor).getSort();
		String type;
		switch (sort)
		{
		case Type.INT:
			type = "ofInt";
		break;
		default:
			throw new RuntimeException("Unknown descriptor: " + descriptor);
		}
		String source = String.format("(cast(%s) vars[$-1].asObject).%s", owner, name);
		writer.writelnUnsafe("vars[$-1] = JavaVar.",type,"(",source,");");
	}

	private static void visitPutField(SourceWriter writer, String owner, String name, String descriptor)
	{
		String source = getAccessor("vars[$-1]", descriptor);
		String destination = String.format("(cast(%s) vars[$-2].asObject).%s", owner, name);
		writer.writelnUnsafe(destination," = ",source,";");
		writer.writelnUnsafe("vars = vars[0 .. $-2];");
	}

	private static String getAccessor(String accessor, String descriptor)
	{
		Type type = Type.getType(descriptor);
		switch (type.getSort())
		{
		case Type.INT:
			return String.format("%s.asInt", accessor);
		default:
			throw new RuntimeException("Unknown descriptor " + descriptor);
		}
	}
}
