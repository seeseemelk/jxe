package be.seeseemelk.jtsc.recompiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import be.seeseemelk.jtsc.recompiler.instructions.InsnDecoder;
import be.seeseemelk.jtsc.types.Visibility;

public class DMethodVisitor extends MethodVisitor
{
	private SourceWriter writer;
	private Deque<String> stack = new LinkedList<>();
	private boolean isStatic = false;
	private Visibility visibility;
	private String name;
	private String className;
	private String returnType;
	private List<String> arguments = Collections.emptyList();
	private final MethodState state;

	public DMethodVisitor(SourceWriter writer)
	{
		super(Opcodes.ASM7);
		this.writer = writer;
		this.state = new MethodState(writer);
	}
	
	public void setStatic(boolean isStatic)
	{
		this.isStatic = isStatic;
	}
	
	public boolean isStatic()
	{
		return this.isStatic;
	}
	
	public boolean isConstructor()
	{
		return name.equals("<init>");
	}
	
	public boolean isStaticInitializer()
	{
		return name.equals("<clinit>");
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setClassName(String name)
	{
		className = name;
	}
	
	public String getClassName()
	{
		return className;
	}
	
	public void setReturnType(String returnType)
	{
		this.returnType = returnType;
		System.out.println("Rettype = " + returnType);
	}
	
	public void setVisibility(Visibility visibility)
	{
		this.visibility = visibility;
	}
	
	public void setArguments(List<String> args)
	{
		this.arguments = args;
	}

	public void setArguments(Type[] argumentTypes)
	{
		this.arguments = Stream.of(argumentTypes)
			.map(type -> Utils.typeToName(type.toString()))
			.collect(Collectors.toList());
	}
	
	public void setFromAccess(int access)
	{
		setStatic(Utils.isStatic(access));
		setVisibility(Visibility.fromAccess(access));
	}
	
	@Override
	public void visitCode()
	{
		List<String> keywords = new ArrayList<>();
		
		if (!isStaticInitializer())
		{
			switch (visibility)
			{
				case PRIVATE:
					keywords.add("private ");
					break;
				case PACKAGE:
					System.err.println("Warning: PACKAGE visibility is not supported, using PUBLIC instead");
				case PUBLIC:
					keywords.add("public ");
					break;
				case PROTECTED:
					keywords.add("protected ");
					break;
			}
		}
		
		if (isStatic || isStaticInitializer())
			keywords.add("static ");
		
		if (isConstructor() || isStaticInitializer())
			keywords.add("this");
		else
		{
			keywords.add(returnType);
			keywords.add(" ");
			keywords.add(name);
		}
		
		keywords.add("(");
		
		int argCount = 0;
		// Non-static functions have a 'this' parameter at index 0.
		int offset = isStatic() ? 0 : 1;
		for (var arg : arguments)
		{
			keywords.add(Utils.getClassName(arg));
			keywords.add(" ");
			keywords.add(state.getVariableName(argCount + offset));
			argCount++;
			if (argCount < arguments.size())
				keywords.add(", ");
		}
		
		keywords.add(") ");
		keywords.add("{");
		
		writer.writelnUnsafe(String.join("", keywords));
		writer.indent();
	}
	
	@Override
	public void visitEnd()
	{
		try
		{
			writer.undent();
			writer.writeln("}");
			writer.writeln();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void visitInsn(int opcode)
	{
		InsnDecoder.visitInsn(state, opcode);
	}
	
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String descriptor)
	{
		owner = Utils.getClassName(owner);
		name = Utils.identifierToD(name);
		switch (opcode)
		{
			case Opcodes.GETSTATIC:
				doGetStatic(owner, name);
				break;
			case Opcodes.GETFIELD:
				doGetField(name);
			case Opcodes.PUTSTATIC:
				doPutStatic(owner, name);
				break;
			case Opcodes.PUTFIELD:
				doPutField(name);
				break;
			default:
				throw new UnsupportedOperationException("Unknown field instruction " + opcode + ", "
						+ owner + ", " + name + ", " + descriptor);
		}
	}
	
	private void doGetStatic(String owner, String field)
	{
		stack.push(owner + "." + field);
	}
	
	private void doGetField(String field)
	{
		var objectRef = stack.pop();
		stack.push(objectRef + "." + field);
	}
	
	private void doPutStatic(String owner, String field)
	{
		var value = stack.pop();
		writer.writelnUnsafe(owner, ".", field, " = ", value, ";");
	}
	
	private void doPutField(String field)
	{
		var value = stack.pop();
		var objectRef = stack.pop();
		writer.writelnUnsafe(objectRef, ".", field, " = ", value, ";");
	}
	
	@Override
	public void visitIincInsn(int var, int increment)
	{
		writer.writelnUnsafe(state.getVariableName(var), " += ", increment);
	}
	
	@Override
	public void visitLdcInsn(Object value)
	{
		if (value instanceof String)
			stack.push("new String(\"" + value + "\")");
		else if (value instanceof Double)
			stack.push(value.toString());
		else if (value instanceof Integer)
			stack.push(value.toString());
		else if (value instanceof Float)
			stack.push(value.toString() + "f");
		else if (value instanceof Type)
			//stack.push("/*Unknown LDC of type 'Type' \"" + value + "\"*/");
			stack.push(Utils.typeToName(((Type) value).toString()) + "._class");
		else
			throw new UnsupportedOperationException("Unknown constant: (" + value.getClass().getSimpleName() + ") " + value);
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface)
	{
		switch (opcode)
		{
			case Opcodes.INVOKESPECIAL:
				if ((opcode & Opcodes.ACC_SUPER) != 0)
				{
					int argCount = Type.getArgumentTypes(descriptor).length;
					LinkedList<String> keywords = new LinkedList<>();
					LinkedList<String> arguments = new LinkedList<>();
					for (int i = 0; i < argCount; i++)
					{
						arguments.push(stack.pop());
					}
					keywords.add("(");
					keywords.add(String.join(", ", arguments));
					keywords.add(")");
					String objectRef = stack.pop(); // Pop 'this' reference
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
					writer.writelnUnsafe(construction);
				}
				else
					throw new UnsupportedOperationException("Cannot perform INVOKESPECIAL without ACC_SUPER");
				//writer.writelnUnsafe(variable + "." + name + "()");
				break;
			case Opcodes.INVOKEVIRTUAL:
			case Opcodes.INVOKEINTERFACE:
				invokeVirtualMethod(Utils.identifierToD(name), descriptor);
				break;
			case Opcodes.INVOKESTATIC:
				invokeStaticMethod(Utils.identifierToD(owner), Utils.identifierToD(name), descriptor);
				break;
			default:
				throw new UnsupportedOperationException("Unknown method: " + opcode + ", " + owner + ", " + name
						+ ", " + descriptor + ", " + isInterface);
		}
	}
	
	private void invokeVirtualMethod(String name, String descriptor)
	{
		List<String> arguments = state.popFromStack(Type.getArgumentTypes(descriptor).length);
		String variable = stack.pop();
		stack.push(variable + "." + name + "(" + String.join(", ", arguments) + ")");
	}
	
	private void invokeStaticMethod(String variable, String name, String descriptor)
	{
		List<String> arguments = state.popFromStack(Type.getArgumentTypes(descriptor).length);
		stack.push(Utils.getClassName(variable) + "." + name + "(" + String.join(", ", arguments) + ")");
	}
	
	@Override
	public void visitVarInsn(int opcode, int var)
	{
		switch (opcode)
		{
			case Opcodes.ALOAD:
				if (var == 0 && !isStatic())
					stack.push("this");
				else
					stack.push(state.getVariableName(var));
				break;
			case Opcodes.ILOAD:
				stack.push(state.getVariableName(var));
				break;
			case Opcodes.ISTORE:
			case Opcodes.LSTORE:
			case Opcodes.FSTORE:
			case Opcodes.DSTORE:
			case Opcodes.ASTORE:
				writer.writelnUnsafe(state.getVariableName(var), " = ", stack.pop(), ";");
				break;
			case Opcodes.DLOAD:
			case Opcodes.FLOAD:
			case Opcodes.LLOAD:
			case Opcodes.RET:
			default:
				throw new UnsupportedOperationException("Unknown opcode: " + opcode + ", " + var);
		}
	}
	
	@Override
	public void visitTypeInsn(int opcode, String type)
	{
		switch (opcode)
		{
			case Opcodes.NEW:
				doNew(type);
				break;
			case Opcodes.CHECKCAST:
				stack.push("checkedCast!(" + Utils.typeToName(type) + ")(" + stack.pop() + ")");
				break;
			case Opcodes.INSTANCEOF:
			case Opcodes.ANEWARRAY:
				String local = state.createLocalVariable();
				//writer.writelnUnsafe("auto ", local, " = _Object.newArray(" + stack.pop() + ");");
				writer.writelnUnsafe("auto ", local, " = new " + type + "[" + stack.pop() + "];");
				stack.push(local);
				break;
			default:
				throw new UnsupportedOperationException("Unknown type: " + opcode + ", " + type);
		}
	}
	
	private void doNew(String type)
	{
		type = Utils.identifierToD(type);
		String var = state.createLocalVariable();
		writer.writelnUnsafe(type + " " + var + ";");
		stack.push(var);
	}
}
