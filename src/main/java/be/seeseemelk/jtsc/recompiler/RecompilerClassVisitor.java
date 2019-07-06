package be.seeseemelk.jtsc.recompiler;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import be.seeseemelk.jtsc.types.BaseType;
import be.seeseemelk.jtsc.types.DecompiledClass;
import be.seeseemelk.jtsc.types.DecompiledField;
import be.seeseemelk.jtsc.types.DecompiledMethod;

class RecompilerClassVisitor extends ClassVisitor
{
	private boolean generateHeader;
	private PrintStream out;
	private DecompiledClass klass;
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
		klass = new DecompiledClass(name);
		
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
			var guardName = klass.mangleType() + "_h";
			out.println("#ifndef " + guardName);
			out.println("#define " + guardName);
			out.println();
		}
		else
		{
			out.println("#include \"" + klass.getClassName() + ".h\"");
		}
		
		out.println("#include <stdbool.h>");
		out.println();
	}
	
	@Override
	public void visitEnd()
	{
		if (generateHeader)
		{
			writeClassFields();
			out.println();
			writeMethodPrototypes();
			out.println();
			out.println("#endif");
		}
	}
	
	@Override
	public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value)
	{
		classFields.add(new DecompiledField(BaseType.findType(descriptor), name));
		return null;
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions)
	{
		/*var method = new DecompiledMethod(klass, name, descriptor);
		methods.add();
		return null;*/
		boolean isStatic = (access & Opcodes.ACC_STATIC) > 0;
		var method = new DecompiledMethod(klass, name, descriptor, isStatic);
		
		/*if (generateHeader)
		{
			methods.add(method);
			return null;
		}
		
		out.print(method.getMethodDefinition());
		out.println(" {");*/
		/*return new MethodNode(Opcodes.ASM7)
		{
			
		}*/
		return new RecompilerMethodVisitor(out, method, methods::add, generateHeader);
	}
	
	private void writeClassFields()
	{
		out.println("struct " + klass.mangleType() + " {");
		out.println("  unsigned int __ref_count;");
		
		for (var classField : classFields)
		{
			out.println("  " + classField.getType().mangleType() + " " + classField.getName() + " = 0;");
		}
		
		out.println("};");
		//out.println("typedef struct " + klass.mangleType() + " " + klass.mangleType() + ";");
	}
	
	private void writeMethodPrototypes()
	{
		for (var method : methods)
		{
			out.print(method.getMethodDefinition());
			out.println(";");
		}
	}
}







