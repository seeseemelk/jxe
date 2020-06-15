package be.seeseemelk.jtsc.recompiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

import be.seeseemelk.jtsc.recompiler.instructions.FieldInsnDecoder;
import be.seeseemelk.jtsc.recompiler.instructions.InsnDecoder;
import be.seeseemelk.jtsc.recompiler.instructions.JumpInsnDecoder;
import be.seeseemelk.jtsc.recompiler.instructions.LdcInsnDecoder;
import be.seeseemelk.jtsc.recompiler.instructions.MethodInsnDecoder;
import be.seeseemelk.jtsc.recompiler.instructions.TypeInsnDecoder;
import be.seeseemelk.jtsc.recompiler.instructions.VarInsnDecoder;
import be.seeseemelk.jtsc.types.Visibility;

public class DMethodVisitor extends MethodVisitor
{
	private SourceWriter writer;
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
		state.setMethodStatic(isStatic);
	}
	
	public boolean isStatic()
	{
		return state.isMethodStatic();
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
		System.out.println("METHOD " + getName());
		
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
		
		if (isStatic() || isStaticInitializer())
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
		InsnDecoder.visit(state, opcode);
	}
	
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String descriptor)
	{
		FieldInsnDecoder.visit(state, opcode, owner, name, descriptor);
	}
	
	@Override
	public void visitIincInsn(int var, int increment)
	{
		writer.writelnUnsafe(state.getVariableName(var), " += ", increment);
	}
	
	@Override
	public void visitLdcInsn(Object value)
	{
		LdcInsnDecoder.visit(state, value);
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface)
	{
		MethodInsnDecoder.visit(state, opcode, owner, name, descriptor, isInterface);
	}
	
	@Override
	public void visitVarInsn(int opcode, int var)
	{
		VarInsnDecoder.visit(state, opcode, var);
	}
	
	@Override
	public void visitTypeInsn(int opcode, String type)
	{
		TypeInsnDecoder.visit(state, opcode, type);
	}
	
	@Override
	public void visitAnnotableParameterCount(int parameterCount, boolean visible)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public AnnotationVisitor visitAnnotationDefault()
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public void visitAttribute(Attribute attribute)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack)
	{
		writer.writelnUnsafe("// Start of frame");
	}
	
	@Override
	public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public void visitIntInsn(int opcode, int operand)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle,
			Object... bootstrapMethodArguments)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public void visitJumpInsn(int opcode, Label label)
	{
		JumpInsnDecoder.visit(state, opcode, label);
	}
	
	@Override
	public void visitLabel(Label label)
	{
		writer.writelnUnsafe(state.getLabelName(label) + ":");
	}
	
	@Override
	public void visitLineNumber(int line, Label start)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end,
			int[] index, String descriptor, boolean visible)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public void visitMaxs(int maxStack, int maxLocals)
	{
		super.visitMaxs(maxStack, maxLocals);
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String descriptor)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public void visitMultiANewArrayInsn(String descriptor, int numDimensions)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public void visitParameter(String name, int access)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
	
	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible)
	{
		throw new UnsupportedOperationException("Not implemented");
	}
}
