package be.seeseemelk.jtsc.recompiler;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.Accessor;
import be.seeseemelk.jtsc.types.BaseType;
import be.seeseemelk.jtsc.types.DecompiledClass;
import be.seeseemelk.jtsc.types.DecompiledField;
import be.seeseemelk.jtsc.types.DecompiledMethod;

class RecompilerClassVisitor extends ClassVisitor
{
	private boolean generateHeader;
	private PrintStream out;
	private DecompiledClass klass;
	private DecompiledClass superKlass;
	private List<DecompiledField> classFields = new ArrayList<>();
	private List<DecompiledMethod> methods = new ArrayList<>();
	
	public RecompilerClassVisitor(OutputStream outputStream, boolean generateHeader)
	{
		super(Opcodes.ASM7);
		out = new PrintStream(outputStream);
		this.generateHeader = generateHeader;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		superKlass = new DecompiledClass(null, superName);
		klass = new DecompiledClass(superKlass, name);
		
		out.println("// Class Name: " + name);
		if (superName != null)
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
			var guardName = klass.mangleType().replace("::", "_") + "_hpp";
			out.println("#ifndef " + guardName);
			out.println("#define " + guardName);
			out.println();
		}
		else
		{
			out.println("#include \"" + klass.getClassName() + ".hpp\"");
		}

		out.println("#include \"jtsc_core.hpp\"");
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
			//writeStructPrototypes();
			//writeVTable();
			//writeClassFields();
			out.println();
			//writeMethodPrototypes();
			out.println();
			out.println("#endif");
		}
	}
	
	@Override
	public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value)
	{
		classFields.add(new DecompiledField(BaseType.findType(descriptor), Accessor.fromAccessInt(access), name));
		return null;
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions)
	{
		boolean isStatic = (access & Opcodes.ACC_STATIC) > 0;
		var method = new DecompiledMethod(klass, name, descriptor, isStatic, Accessor.fromAccessInt(access));
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
		printfln("class %s : public %s {", klass.getClassName(), superKlass.mangleName());
		var accessor = Accessor.UNSPECIFIED;
		
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
			
			if (method.getName().equals("<init>"))
				printfln("    %s(%s);", klass.getClassName(), method.getParameterDefinitions());
			else
				printfln("    %s;", method.getShortMethodDefinition());
		}
		
		printfln("};");
	}
	
	private void writeNamespaceStart()
	{
		for (var part : klass.getNamespaceParts())
		{
			printfln("namespace %s {%n", part);
		}
	}
	
	private void writeNamespaceEnd()
	{
		println();
		for (var part : klass.getNamespaceParts())
		{
			printfln("}%n", part);
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







