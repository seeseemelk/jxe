package be.seeseemelk.jtsc.recompiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class DClassVisitor extends ClassVisitor
{
	private SourceWriter writer;
	private Path rootDirectory;
	private Set<String> imports;
	private String className;
	private boolean hasMain = false;
	
	public DClassVisitor(Path rootDirectory, Set<String> imports)
	{
		super(Opcodes.ASM7);
		this.rootDirectory = rootDirectory;
		this.imports = imports;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		super.visit(version, access, name, signature, superName, interfaces);
		System.out.println("Converting " + name);
	
		try
		{
			String packageName = Utils.getPackageName(name);
			className = Utils.getClassName(name);
			String superClassName = Utils.getClassName(superName);
			
			Path outputDirectory = rootDirectory.resolve(packageName);
			Files.createDirectories(outputDirectory);
			Path outputFile = outputDirectory.resolve(className + ".d");
			writer = new SourceWriter(Files.newBufferedWriter(outputFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
			
			writer.writeln("// Class name: ", name);
			writer.writeln("// Version: ", version);
			writer.writeln("// Access: ", access);
			if (signature != null)
				writer.writeln("// Signature: ", signature);
			writer.writeln("// Super Name: ", superName);
			
			if (!packageName.isEmpty())
			{
				writer.writeln("module ", packageName.replace('/', '.'), ";");
				writer.writeln();
			}
			
			for (String toImport : imports)
			{
				writer.writeln("import ", toImport, ";");
			}
			writer.writeln();
			
			writer.writeln(Utils.accessorToString(access), " class ", className, " : ", superClassName, " {");
			writer.indent();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions)
	{
		var visitor = new DMethodVisitor(writer);
		visitor.setFromAccess(access);
		visitor.setName(name);
		visitor.setReturnType(Utils.typeToName(Type.getReturnType(descriptor).toString()));
		visitor.setArguments(Type.getArgumentTypes(descriptor));
		
		if (visitor.isStatic() && name.equals("main"))
		{
			hasMain = true;
		}
		
		return visitor;
	}
	
	@Override
	public void visitEnd()
	{
		try
		{
			writer.undent();
			writer.write("}\n");
			
			if (hasMain)
			{
				writer.writeln("void main(string[] args) {");
				writer.indent();
				writer.writeln(className + ".main();");
				writer.undent();
				writer.writeln("}");
			}
			
			writer.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}