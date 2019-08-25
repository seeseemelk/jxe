package be.seeseemelk.jxe.discovery;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
	private Optional<DecompiledClass> optionalPartial = Optional.empty();
	private Optional<DecompiledClass> decompiledClass = Optional.empty();
	private String filename;
	
	public ClassDiscoverer(String filename)
	{
		super(Opcodes.ASM7);
		this.filename = filename;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		if (name.equals("module-info") || name.equals("java/lang/Object"))
			return;
		
		if (superName.equalsIgnoreCase("java/lang/Object;"))
		{
			optionalPartial = Optional.of(new DecompiledClass(DecompiledClass.OBJECT, name));
		}
		else
		{
			var superKlass = new DecompiledClass(superName);
			optionalPartial = Optional.of(new DecompiledClass(superKlass, name));
		}
		
		System.out.format("CLASS %s%n", optionalPartial.get().getFullyQualifiedName());
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions)
	{
		if (optionalPartial.isEmpty())
			return null;
		var partial = optionalPartial.get();
		
		boolean isStatic = Protection.isStatic(access);
		var partialMethod = new DecompiledMethod(partial, name, descriptor, isStatic, Protection.fromProtectionInt(access));
		return new MethodDiscoverer(filename, partialMethod, discoveredMethod -> {
			System.out.format("METHOD %s%n", discoveredMethod.getName());
			partial.addMethod(discoveredMethod);
		});
	}
	
	@Override
	public void visitEnd()
	{
		if (optionalPartial.isEmpty())
			return;
		var partial = optionalPartial.get();
		
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
			return discoverClass(file.toString(), inputStream);
		}
	}
	
	public static ClassDiscoverer discoverClass(String filename, InputStream input) throws IOException
	{
		System.out.println("FILE " + filename);
		var reader = new ClassReader(input);
		var classDiscoverer = new ClassDiscoverer(filename.toString());
		reader.accept(classDiscoverer, 0);
		return classDiscoverer;
	}
}
