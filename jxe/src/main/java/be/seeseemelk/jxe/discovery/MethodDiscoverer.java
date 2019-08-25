package be.seeseemelk.jxe.discovery;

import java.util.function.Consumer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import be.seeseemelk.jxe.FilePosition;
import be.seeseemelk.jxe.api.Pure;
import be.seeseemelk.jxe.types.DecompiledMethod;
import be.seeseemelk.jxe.types.FieldAccess;
import be.seeseemelk.jxe.types.FieldReference;
import be.seeseemelk.jxe.types.MethodReference;
import be.seeseemelk.jxe.types.VariableAccess.Action;

public class MethodDiscoverer extends MethodVisitor
{
	private final DecompiledMethod partial;
	private final Consumer<DecompiledMethod> callback;
	private final String filename;
	private int lineNumber = 0;
	
	public MethodDiscoverer(String filename, DecompiledMethod method, Consumer<DecompiledMethod> callback)
	{
		super(Opcodes.ASM7);
		this.filename = filename;
		this.partial = method;
		this.callback = callback;
	}
	
	private FilePosition getFilePosition()
	{
		return new FilePosition(filename, lineNumber);
	}
	
	@Override
	public void visitLineNumber(int line, Label start)
	{
		lineNumber = line;
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible)
	{
		var type = Type.getType(descriptor);
		if (type.getClassName().equals(Pure.class.getName()))
		{
			partial.addFlag(Flag.PURE);
		}
		return null;
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface)
	{
		var reference = new MethodReference(getFilePosition(), owner, name, Type.getMethodType(descriptor));
		partial.addMethodReference(reference);
	}
	
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String descriptor)
	{
		var position = getFilePosition();
		switch (opcode)
		{
			case Opcodes.GETSTATIC -> partial.addFieldAccess(FieldAccess.withStatic(position, Action.READ, new FieldReference(owner, name)));
			case Opcodes.PUTSTATIC -> partial.addFieldAccess(FieldAccess.withStatic(position, Action.WRITE, new FieldReference(owner, name)));
			case Opcodes.GETFIELD -> partial.addFieldAccess(FieldAccess.withObject(position, Action.READ, new FieldReference(owner, name)));
			case Opcodes.PUTFIELD -> partial.addFieldAccess(FieldAccess.withObject(position, Action.WRITE, new FieldReference(owner, name)));
			default -> throw new UnsupportedOperationException("Unsupported instruction");
		}
	}

	@Override
	public void visitEnd()
	{
		callback.accept(partial);
	}
}
