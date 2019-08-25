package be.seeseemelk.jxe.recompiler;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import be.seeseemelk.jxe.Protection;
import be.seeseemelk.jxe.types.BaseType;
import be.seeseemelk.jxe.types.ClassImport;
import be.seeseemelk.jxe.types.DecompiledClass;
import be.seeseemelk.jxe.types.DecompiledField;
import be.seeseemelk.jxe.types.DecompiledMethod;

class RecompilerClassVisitor extends ClassVisitor
{
	private final boolean generateHeader;
	private final PrintStream out;
	private final Set<ClassImport> imports;
	private DecompiledClass klass;
	private DecompiledClass superKlass;
	private List<DecompiledField> classFields = new ArrayList<>();
	private List<DecompiledMethod> methods = new ArrayList<>();
	
	public RecompilerClassVisitor(OutputStream outputStream, Set<ClassImport> imports, boolean generateHeader)
	{
		super(Opcodes.ASM7);
		out = new PrintStream(outputStream);
		
		this.imports = imports;
		this.generateHeader = generateHeader;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		superKlass = new DecompiledClass(null, superName);
		klass = new DecompiledClass(superKlass, name);
		
		out.println("// Class Name: " + name);
		
		if (!klass.isJavaLangObject())
				out.println("// Super Class: " + superName);
		
		if (signature != null)
			out.println("// Signature: " + superName);
		if (interfaces != null && interfaces.length > 0)
		{			
			out.println("// Implementing interfaces: ");
			for (var interfaceName : interfaces)
				out.println("//  - " + interfaceName);
		}
		
		if (generateHeader)
		{
			var guardName = klass.getFullyQualifiedName().replace("/", "_") + "_hpp";
			out.println("#ifndef " + guardName);
			out.println("#define " + guardName);
			out.println();
		}
		else
		{
			printfln("#include \"%s.hpp\"", klass.getFullyQualifiedName());
		}

		for (var classImport : imports)
		{
			if (!generateHeader || classImport.isPublic())
			{
				printfln("#include \"%s\"", classImport.getClassFqn());
			}
		}
		out.println();
		
		if (generateHeader)
			writeNamespaceStart();
	}
	
	@Override
	public void visitEnd()
	{
		if (generateHeader)
		{
			writeClass();
			writeNamespaceEnd();
			out.println();
			out.println();
			out.println("#endif");
		}
	}
	
	@Override
	public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value)
	{
		classFields.add(new DecompiledField(BaseType.findType(descriptor).getValue0(), Protection.fromProtectionInt(access), name));
		return null;
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions)
	{
		boolean isStatic = (access & Opcodes.ACC_STATIC) > 0;
		var method = new DecompiledMethod(klass, name, descriptor, isStatic, Protection.fromProtectionInt(access));
		return new RecompilerMethodVisitor(out, method, methods::add, generateHeader);
	}
	
	/*private void writeStructPrototypes()
	{
		out.println("// Class struct prototypes");
		out.println("struct " + klass.mangleType() + ";");
		out.println("struct vtable_" + klass.mangleName() + ";");
		out.println();
	}
	
	private void writeVTable()
	{
		out.println("// Class struct definitions");
		out.println("struct vtable_" + klass.mangleName() + " {");
		
		for (var method : methods)
			if (!method.isStaticMethod())
				out.printf("  %s;%n", method.asNamedPointer());
		
		out.println("};");
		out.println("");
	}
	
	private void writeClassFields()
	{
		out.println("struct " + klass.mangleType() + " {");
		//out.println("  unsigned int __ref_count;");
		out.printf("  struct %s *super;%n", superKlass.mangleType());
		
		for (var classField : classFields)
		{
			out.println("  " + classField.getType().mangleType() + " " + classField.getName() + " = 0;");
		}
		
		out.println("};");
		//out.println("typedef struct " + klass.mangleType() + " " + klass.mangleType() + ";");
	}
	
	private void writeMethodPrototypes()
	{
		out.println("// Method prototypes");
		for (var method : methods)
		{
			out.print(method.getMethodDefinition());
			out.println(";");
		}
	}*/
	
	private void writeClass()
	{
		if (klass.isJavaLangObject())
			printfln("class %s {", klass.getClassName());
		else
			printfln("class %s : public %s {", klass.getClassName(), superKlass.mangleName());
		var accessor = Protection.UNSPECIFIED;
		
		printfln("// Class fields");
		for (var classField : classFields)
		{
			if (accessor != classField.getAccessor())
			{
				accessor = classField.getAccessor();
				printfln("%s:", accessor.toString().toLowerCase());
			}
			
			printfln("    %s %s;", classField.getType().mangleType(), classField.getName());
		}
		
		println();
		printfln("// Methods");
		for (var method : methods)
		{
			if (accessor != method.getAccessor())
			{
				accessor = method.getAccessor();
				printfln("%s:", accessor.toString().toLowerCase());
			}
			
			/*if (method.getName().equals("<init>"))
				printfln("    %s(%s);", klass.getClassName(), method.getParameterDefinitions());
			else
				printfln("    %s;", method.getShortMethodDefinition());*/
		}
		
		printfln("};");
		println();
	}
	
	private void writeNamespaceStart()
	{
		for (var part : klass.getNamespaceParts())
		{
			printfln("namespace %s {", part);
		}
		println();
	}
	
	private void writeNamespaceEnd()
	{
		for (var part : klass.getNamespaceParts())
		{
			printfln("}", part);
		}
	}
	
	private void printfln(String fmt, Object... args)
	{
		out.printf(fmt, args);
		out.println();
	}
	
	private void println()
	{
		out.println();
	}
}





