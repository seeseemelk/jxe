package be.seeseemelk.jxe.discovery;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import be.seeseemelk.jxe.Protection;
import be.seeseemelk.jxe.types.DecompiledClass;
import be.seeseemelk.jxe.types.DecompiledMethod;

public class ClassDiscoverer extends ClassVisitor
{
	private DecompiledClass partial;
	private Optional<DecompiledClass> decompiledClass;
	private String filename;
	
	public ClassDiscoverer(String filename)
	{
		super(Opcodes.ASM7);
		this.filename = filename;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		if (superName.equalsIgnoreCase("java/lang/Object;"))
			partial = new DecompiledClass(DecompiledClass.OBJECT, name);
		else
		{
			var superKlass = new DecompiledClass(superName);
			partial = new DecompiledClass(superKlass, name);
		}
		
		System.out.format("DISCOVER CLASS %s%n", partial.getFullyQualifiedName());
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions)
	{
		boolean isStatic = Protection.isStatic(access);
		var partialMethod = new DecompiledMethod(partial, name, descriptor, isStatic, Protection.fromProtectionInt(access));
		return new MethodDiscoverer(filename, partialMethod, discoveredMethod -> {
			System.out.format("DISCOVER METHOD %s%n", discoveredMethod.getName());
			partial.addMethod(discoveredMethod);
		});
	}
	
	@Override
	public void visitEnd()
	{
		decompiledClass = Optional.of(partial);
	}
	
	public Optional<DecompiledClass> getDecompiledClass()
	{
		return decompiledClass;
	}
	
	public static ClassDiscoverer discoverClass(Path file) throws IOException
	{
		try (var inputStream = new FileInputStream(file.toFile()))
		{
			var reader = new ClassReader(inputStream);
			var classDiscoverer = new ClassDiscoverer(file.toString());
			reader.accept(classDiscoverer, 0);
			return classDiscoverer;
		}
	}
}
