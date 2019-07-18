package be.seeseemelk.jtsc.recompiler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.javatuples.Pair;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.Accessor;
import be.seeseemelk.jtsc.types.ClassImport;
import be.seeseemelk.jtsc.types.DecompiledClass;
import be.seeseemelk.jtsc.types.DecompiledMethod;

public class FirstPassClassVisitor extends ClassVisitor
{
	private final Path input;
	private final Path output;
	//private final Consumer<Pair<Path, List<ClassImport>>> callback;
	private final Consumer<Path> callback;
	private DecompiledClass klass;
	private DecompiledClass superKlass;
	private final Set<String> publicImports = new HashSet<>();
	private final Set<String> privateImports = new HashSet<>();

	public FirstPassClassVisitor(Path input, Path output, Consumer<Path> callback)
	{
		super(Opcodes.ASM7);
		
		this.input = input;
		this.output = output;
		this.callback = callback;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		superKlass = new DecompiledClass(null, superName);
		klass = new DecompiledClass(superKlass, name);
		
		publicImports.add(superKlass.getFullyQualifiedName());
	}
	
	@Override
	public void visitEnd()
	{
		try
		{
			var dir = output;
			for (var directory : klass.getNamespaceParts())
				dir = dir.resolve(directory);
			System.out.println("DIR " + dir.toString());
			Files.createDirectories(dir);
			
			generateHeader(dir);
			callback.accept(generateSource(dir));
		}
		catch (IOException e)
		{
			System.err.println("Failed to create directory for file: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions)
	{
		boolean isStatic = (access & Opcodes.ACC_STATIC) > 0;
		var method = new DecompiledMethod(klass, name, descriptor, isStatic, Accessor.fromAccessInt(access));
		
		if (method.getReturnType() instanceof DecompiledClass)
		{
			var returnType = (DecompiledClass) method.getReturnType();
			publicImports.add(returnType.getFullyQualifiedName());
		}
		
		method.getParameterTypes().stream()
			.filter(parameter -> parameter instanceof DecompiledClass)
			.map(parameter -> (DecompiledClass) parameter)
			.map(DecompiledClass::getFullyQualifiedName)
			.forEach(publicImports::add);
		
		return null;
	}
	
	private Set<ClassImport> getImports()
	{
		var imports = new TreeSet<ClassImport>();
		
		publicImports.stream()
				.map(ClassImport::makePublic)
				.forEach(imports::add);
		
		privateImports.stream()
				.map(ClassImport::makePrivate)
				.forEach(imports::add);
		
		return imports;
	}
	
	private void generateHeader(Path output) throws IOException
	{
		try (var inputStream = new FileInputStream(input.toFile()))
		{
			try (var outputStream = new FileOutputStream(output.resolve(klass.getClassName() + ".hpp").toFile()))
			{
				var reader = new ClassReader(inputStream);
				reader.accept(new RecompilerClassVisitor(outputStream, getImports(), true), 0);
			}
		}
	}
	
	private Path generateSource(Path output) throws IOException
	{
		try (var inputStream = new FileInputStream(input.toFile()))
		{
			try (var outputStream = new FileOutputStream(output.resolve(klass.getClassName() + ".cpp").toFile()))
			{
				var reader = new ClassReader(inputStream);
				reader.accept(new RecompilerClassVisitor(outputStream, getImports(), false), 0);
			}
		}
		
		return output.resolve(klass.getClassName() + ".cpp");
	}

}













