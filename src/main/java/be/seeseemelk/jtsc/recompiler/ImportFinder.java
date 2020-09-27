package be.seeseemelk.jtsc.recompiler;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Finds all relevant imports for a file.
 * @author seeseemelk
 *
 */
public class ImportFinder extends ClassVisitor
{
	private Set<String> imports = new TreeSet<>();
	private Set<String> excludes = new TreeSet<>();
	
	private class MethodImportFinder extends MethodVisitor
	{
		public MethodImportFinder()
		{
			super(Opcodes.ASM7);
		}
		
		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String descriptor)
		{
			imports.add(owner.replace('/', '.'));
		}
	}
	
	public ImportFinder()
	{
		super(Opcodes.ASM7);
		imports.add("java.lang.Object");
		imports.add("java.lang.String");
	}
	
	/**
	 * Gets all relevant imports for a file.
	 * @return
	 */
	public Set<String> getImports()
	{
		imports.removeAll(excludes);
		return Collections.unmodifiableSet(imports);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions)
	{
		var type = Type.getReturnType(descriptor);
		if (type.getSort() == Type.OBJECT)
		{
			imports.add(Utils.identifierToD(Type.getReturnType(descriptor).getClassName()));
		}
		return new MethodImportFinder();
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		imports.add(Utils.identifierToD(superName));
		excludes.add(Utils.identifierToD(name));
	}
}