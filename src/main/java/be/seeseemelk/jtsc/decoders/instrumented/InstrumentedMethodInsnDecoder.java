package be.seeseemelk.jtsc.decoders.instrumented;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import be.seeseemelk.jtsc.recompiler.MethodDescriptor;
import be.seeseemelk.jtsc.recompiler.SourceWriter;
import be.seeseemelk.jtsc.recompiler.Utils;

public final class InstrumentedMethodInsnDecoder
{
	private InstrumentedMethodInsnDecoder() {}

	private static void visitInvokeSpecial(
			MethodDescriptor methodDescriptor,
			SourceWriter writer,
			int opcode,
			String owner,
			String descriptor
	) throws IOException
	{
		if ((opcode & Opcodes.ACC_SUPER) != 0)
		{
			writer.writelnUnsafe("super.__construct();");
			writer.writelnUnsafe("vars = vars[0 .. $-1];");
			/*int argCount = Type.getArgumentTypes(descriptor).length;
			LinkedList<String> keywords = new LinkedList<>();
			LinkedList<String> arguments = new LinkedList<>();
			for (int i = 0; i < argCount; i++)
			{
				arguments.push("vars[$-" + (i + 1) + "]");
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
			state.getWriter().writeln(construction);*/
		}
		else
			throw new UnsupportedOperationException("Cannot perform INVOKESPECIAL without ACC_SUPER");
	}


	public static void visit(
			MethodDescriptor methodDescriptor,
			SourceWriter writer,
			int opcode,
			String owner,
			String name,
			String descriptor,
			boolean isInterface
	)
	{
		try
		{
			switch (opcode)
			{
				case Opcodes.INVOKESPECIAL:
					visitInvokeSpecial(methodDescriptor, writer, opcode, owner, descriptor);
				break;
				case Opcodes.INVOKEVIRTUAL:
				case Opcodes.INVOKEINTERFACE:
					visitInvokeVirtual(methodDescriptor, writer, Utils.getClassName(owner), Utils.identifierToD(name), descriptor);
				break;
				case Opcodes.INVOKESTATIC:
					visitInvokeStatic(writer, Utils.getClassName(owner), Utils.identifierToD(name), descriptor);
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

	private static void visitInvokeStatic(SourceWriter writer, String owner, String name, String descriptor)
	{
		writer.writelnUnsafe("// Owner: ",owner);
		writer.writelnUnsafe("// Name: ",name);
		writer.writelnUnsafe("// Descriptor: ",descriptor);
		Type type = Type.getMethodType(descriptor);

		String call = owner+"."+name+"("+getArguments(type)+")";
		if (type.getReturnType().getSort() == Type.VOID)
		{
			writer.writelnUnsafe(call,";");
		}
		else
		{
			String wrapped = wrapType(call, type.getReturnType());
			writer.writelnUnsafe("vars ~= ",wrapped,";");
		}
		popArguments(writer, type);
	}

	private static void visitInvokeVirtual(
			MethodDescriptor methodDescriptor,
			SourceWriter writer,
			String owner,
			String name,
			String descriptor
	)
	{
		writer.writelnUnsafe("// Invoke virtual");
		writer.writelnUnsafe("// Descriptor: ",descriptor);
		var type = Type.getMethodType(descriptor);

		String object = "(cast("+owner+") vars[$-1].asObject)";
		String call = object+"."+name+"("+getArguments(type)+")";
		if (type.getReturnType().getSort() == Type.VOID)
		{
			writer.writelnUnsafe(call,";");
		}
		else
		{
			String wrapped = wrapType(call, type.getReturnType());
			writer.writelnUnsafe("vars ~= ",wrapped,";");
		}
		popArguments(writer, type);
	}

	public static void visitDynamic(
			SourceWriter writer,
			String name,
			String descriptor,
			Handle bootstrapMethodHandle,
			Object[] bootstrapMethodArguments
	)
	{
		writer.writelnUnsafe("// Visit dynamic");
//		try
//		{
//			var delegate = state.createLocalVariable();
//
//			Type type = Type.getType(descriptor);
//			String returnType = Utils.getClassName(type.getReturnType().getInternalName());
//
//			// Create static delegate
//			var writer = state.getWriter();
//			writer.writeln("static ", returnType, " delegate() ", delegate, " = null;");
//			writer.writeln("if (", delegate, " is null) {");
//			writer.indent();
//
//			// Initialize delegate
//			var targetClass = Utils.getClassName(bootstrapMethodHandle.getOwner());
//			var targetMethod = Utils.identifierToD(bootstrapMethodHandle.getName());
//			Type method = (Type) bootstrapMethodArguments[2];
//			String lambdaReturnType = Utils.getClassName(method.getArgumentTypes()[0].getClassName());
//			writer.writeln(
//					delegate,
//					" = ",
//					targetClass,
//					".",
//					targetMethod,
//					"((val) => this.",
//					Utils.identifierToD(((Handle) bootstrapMethodArguments[1]).getName()),
//					"(cast(", lambdaReturnType ,") val));"
//			);
//
//			writer.undent();
//			writer.writeln("}");
//
//			// Perform call
//			state.pushToStack(delegate + "()");
//		}
//		catch (Exception e)
//		{
//			throw new RuntimeException("Exception occured while processing invokedynamic", e);
//		}
	}

	private static String wrapType(String call, Type type)
	{
		switch (type.getSort())
		{
		case Type.BOOLEAN:
			return "JavaVar.ofInt((" + call + " == true) ? 1 : 0)";
		case Type.INT:
			return "JavaVar.ofInt(" + call + ")";
		case Type.OBJECT:
			return "JavaVar.ofObject(cast(_Object) (" + call + "))";
		default:
			throw new RuntimeException(String.format("Unsupported sort: %d", type.getSort()));
		}
	}

	private static String getArguments(Type type)
	{
		List<String> arguments = new ArrayList<>();
		Type[] types = type.getArgumentTypes();
		for (int i = 0; i < types.length; i++)
		{
			String argument = "vars[$ - " + (i + 1) + "]";
			switch (types[i].getSort())
			{
			case Type.INT:
				argument += ".asInt";
			break;
			case Type.OBJECT:
				argument += ".asObject";
			break;
			default:
				throw new RuntimeException(String.format("Unsupported sort: %d", types[i].getSort()));
			}
			arguments.add(argument);
		}
		return String.join(", ", arguments);
	}

	private static void popArguments(SourceWriter writer, Type type)
	{
		Type[] types = type.getArgumentTypes();
		if (types.length > 0)
			writer.writelnUnsafe("vars = vars[0 .. $-",types.length,"];");
	}
}
