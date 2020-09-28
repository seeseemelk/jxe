package be.seeseemelk.jtsc.recompiler;

import java.util.function.Consumer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

import be.seeseemelk.jtsc.instructions.ConditionalInstruction;
import be.seeseemelk.jtsc.instructions.FieldInstruction;
import be.seeseemelk.jtsc.instructions.IntegerIncrementInstruction;
import be.seeseemelk.jtsc.instructions.InvokeDynamicInstruction;
import be.seeseemelk.jtsc.instructions.LoadConstantInstruction;
import be.seeseemelk.jtsc.instructions.MethodCallInstruction;
import be.seeseemelk.jtsc.instructions.TypeInstruction;
import be.seeseemelk.jtsc.instructions.UnconditionalInstruction;
import be.seeseemelk.jtsc.instructions.VarInstruction;
import be.seeseemelk.jtsc.instructions.ZeroArgInstruction;

public class StreamMethodVisitor extends MethodVisitor
{
	private InstructionStream stream = new InstructionStream();
	private Consumer<InstructionStream> callback = (stream) -> {};
	
	public StreamMethodVisitor()
	{
		super(Opcodes.ASM7);
	}
	
	public InstructionStream getInstructionStream()
	{
		return stream;
	}
	
	public void setCallback(Consumer<InstructionStream> callback)
	{
		this.callback = callback;
	}
	
	@Override
	public void visitCode()
	{
	}
	
	@Override
	public void visitEnd()
	{
		callback.accept(stream);
	}

	@Override
	public void visitInsn(int opcode)
	{
		stream.add(new ZeroArgInstruction(opcode));
	}
	
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String descriptor)
	{
		stream.add(new FieldInstruction(opcode, owner, name, descriptor));
	}
	
	@Override
	public void visitIincInsn(int var, int increment)
	{
		stream.add(new IntegerIncrementInstruction(var, increment));
	}
	
	@Override
	public void visitLdcInsn(Object value)
	{
		stream.add(new LoadConstantInstruction(value));
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface)
	{
		stream.add(new MethodCallInstruction(opcode, owner, name, descriptor, isInterface));
	}
	
	@Override
	public void visitVarInsn(int opcode, int var)
	{
		stream.add(new VarInstruction(opcode, var));
	}
	
	@Override
	public void visitTypeInsn(int opcode, String type)
	{
		stream.add(new TypeInstruction(opcode, type));
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
		//throw new UnsupportedOperationException("Not implemented");
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
		stream.add(
				new InvokeDynamicInstruction(
						name,
						descriptor,
						bootstrapMethodHandle,
						bootstrapMethodArguments
				)
		);
	}
	
	@Override
	public void visitJumpInsn(int opcode, Label label)
	{
		switch (opcode)
		{
			case Opcodes.IFEQ:
			case Opcodes.IFNE:
			case Opcodes.IFLT:
			case Opcodes.IFGE:
			case Opcodes.IFGT:
			case Opcodes.IFLE:
			case Opcodes.IF_ICMPEQ:
			case Opcodes.IF_ICMPNE:
			case Opcodes.IF_ICMPLT:
			case Opcodes.IF_ICMPGE:
			case Opcodes.IF_ICMPGT:
			case Opcodes.IF_ICMPLE:
			case Opcodes.IF_ACMPEQ:
			case Opcodes.IF_ACMPNE:
			case Opcodes.IFNULL:
			case Opcodes.IFNONNULL:
				stream.add(new ConditionalInstruction(opcode, label));
				break;
			case Opcodes.GOTO:
				stream.add(new UnconditionalInstruction(opcode, label));
				break;
			case Opcodes.JSR:
			default:
				throw new UnsupportedOperationException("Not implemented");
		}
	}
	
	@Override
	public void visitLabel(Label label)
	{
		stream.addLabel(label);
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
