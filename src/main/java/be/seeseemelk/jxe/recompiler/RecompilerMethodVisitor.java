package be.seeseemelk.jxe.recompiler;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import be.seeseemelk.jxe.Accessor;
import be.seeseemelk.jxe.types.BaseType;
import be.seeseemelk.jxe.types.DecompiledClass;
import be.seeseemelk.jxe.types.DecompiledMethod;
import be.seeseemelk.jxe.types.PrimitiveType;
import be.seeseemelk.jxe.types.PrimitiveType.PType;
import be.seeseemelk.jxe.types.StringType;
import be.seeseemelk.jxe.types.VariableType;

class RecompilerMethodVisitor extends MethodNode
{
	private static final String INDENT = "    ";
	private boolean generateHeader;
	private PrintStream out;
	private Consumer<DecompiledMethod> callback;
	private DecompiledMethod method;
	private Deque<BaseType> stack = new ArrayDeque<>();
	private int labelCount = 0;
	private boolean readyForCode = false;
	
	public RecompilerMethodVisitor(PrintStream output, DecompiledMethod method,
			Consumer<DecompiledMethod> callback, boolean generateHeader)
	{
		super(Opcodes.ASM7);
		this.out = output;
		this.callback = callback;
		this.method = method;
		this.generateHeader = generateHeader;
	}
	
	@Override
	public void visitEnd()
	{
		if (!generateHeader)
		{
			if (method.getName().equals("<init>"))
			{
				out.print(method.getOwner().mangleName());
				out.print("::");
				out.print(method.getOwner().getClassName());
				out.print("(");
				out.print(method.getParameterDefinitions());
				out.print(")");
				// We expect a call to the super constructor still, unless we're java.lang.Object.
				if (method.getOwner().isJavaLangObject())
					out.println(" {");
			}
			else
			{
				out.print(method.getLongMethodDefinition());
				out.println(" {");
				readyForCode = true;
			}
			
			// Find all labels that are actually used.
			instructions.iterator().forEachRemaining(node ->
			{
				switch (node.getType())
				{
					case AbstractInsnNode.JUMP_INSN -> preprocessJumpInsn((JumpInsnNode) node);
				}
			});
			
			// Process all instructions.
			instructions.iterator().forEachRemaining(node ->
			{
				switch (node.getType())
				{
					case AbstractInsnNode.LABEL -> processLabel((LabelNode) node);
					case AbstractInsnNode.LINE -> processLineNumber((LineNumberNode) node);
					case AbstractInsnNode.VAR_INSN -> processVarInsn((VarInsnNode) node); 
					case AbstractInsnNode.METHOD_INSN -> processMethodInsn((MethodInsnNode) node);
					case AbstractInsnNode.INSN -> processInsn((InsnNode) node);
					case AbstractInsnNode.INT_INSN -> processIntInsn((IntInsnNode) node);
					case AbstractInsnNode.JUMP_INSN -> processJumpInsn((JumpInsnNode) node);
					case AbstractInsnNode.FRAME -> {}
					case AbstractInsnNode.FIELD_INSN -> processFieldInsn((FieldInsnNode) node);
					case AbstractInsnNode.TYPE_INSN -> processTypeInsn((TypeInsnNode) node);
					case AbstractInsnNode.LDC_INSN -> processLdcInsn((LdcInsnNode) node);
					default ->
						throw new UnsupportedOperationException(String.format("Instruction 0x%X (%s) is not supported", node.getOpcode(), node.getClass().getSimpleName()));
				}
			});
			
			out.println("}");
			out.println();
			
			// If this is a main function, let's generate a function called main.
			if (method.isMain())
			{
				out.println("int main() {");
				printfln("%s();", method.mangleLongName());
				out.println("}");
			}
		}
		
		callback.accept(method);
	}
	
	private void processLabel(LabelNode node)
	{
		if (((LabelState) node.getLabel().info).isUsed)
			printfln("%s:", getLabel(node));
	}
	
	private void processLineNumber(LineNumberNode node)
	{
		if (readyForCode)
			printfln("// Line %d", node.line);
	}
	
	private void processVarInsn(VarInsnNode node)
	{	
		switch (node.getOpcode())
		{
			case Opcodes.ISTORE -> {
				var type = stack.pop();
				if (type instanceof PrimitiveType)
				{
					var primitive = (PrimitiveType) type;
					printfln("%s %s = %s;", type.mangleType(), getVariable(node.var), primitive.getValue().get());					
				}
			}
			case Opcodes.ILOAD -> stack.push(new VariableType(getVariable(node.var)));
			case Opcodes.ALOAD -> stack.push(new VariableType(getVariable(node.var)));
			default -> throw new RuntimeException(String.format("Unsupported VarInsn opcode 0x%X %d", node.getOpcode(), node.var));
		}
	}
	
	private void processMethodInsn(MethodInsnNode node)
	{
		switch (node.getOpcode())
		{
			case Opcodes.INVOKESPECIAL -> {
				if (method.getName().equals("<init>") && node.name.equals("<init>"))
				{
					// java.lang.Object should not be calling its own super constructor.
					if (method.getOwner().isJavaLangObject())
						break;
					
					var targetClass = new DecompiledClass(null, node.owner);
					var target = new DecompiledMethod(targetClass, node.name, node.desc, true, Accessor.UNSPECIFIED);
					
					var targetName = targetClass.getClassName();

					var builder = new StringBuilder();
					var arguments = new String[target.getParameterTypes().size()];
					for (int i = 0; i < arguments.length; i++)
						arguments[i] = stack.pop().asValue();
					builder.append(String.join(", ", arguments));
					
					out.println();
					printfln("%s: %s(%s) {", INDENT, targetName, builder);
					readyForCode = true;
				}
				else
					System.err.println("WARNING: INVOKESPECIAL (0xB7) to " + node.name + " is not yet supported");
			}
			case Opcodes.INVOKESTATIC -> {
				var targetClass = new DecompiledClass(null, node.owner);
				var target = new DecompiledMethod(targetClass, node.name, node.desc, true, Accessor.UNSPECIFIED);
				
				if (targetClass.getFullyQualifiedName().equals("be/seeseemelk/jxe/api/JXE"))
				{
					invocationToJXEInternal(node, target);
				}
				else
				{
					StringBuilder builder = new StringBuilder(target.mangleLongName()).append('(');
					
					//var parameters = (String[]) target.getParameterTypes().stream().map(BaseType::asValue).toArray();
					var arguments = new String[target.getParameterTypes().size()];
					for (int i = 0; i < arguments.length; i++)
						arguments[i] = stack.pop().asValue();
					builder.append(String.join(", ", arguments)).append(')');
					
					stack.push(new VariableType(builder.toString()));
				}
			}
			default -> throw new RuntimeException(String.format("Unsupported MethodInsn opcode 0x%X %s", node.getOpcode(), node.name));
		}
	}
	
	private void processInsn(InsnNode node)
	{
		switch (node.getOpcode())
		{
			case Opcodes.RETURN -> {
				if (!stack.isEmpty())
					printfln("%s;", stack.pop().asValue());
				if (!stack.isEmpty())
					throw new IllegalStateException("Stack should be empty");
				if (!method.getName().equals("<init>"))
					println("return;");
			}
			case Opcodes.IRETURN, Opcodes.ARETURN -> printfln("return %s;", stack.pop().asValue());
			case Opcodes.IADD -> binaryExpression("+");
			case Opcodes.ISUB -> binaryExpression("-");
			case Opcodes.IMUL -> binaryExpression("*");
			case Opcodes.IDIV -> binaryExpression("/");
			case Opcodes.ICONST_M1 -> produceIconst(-1);
			case Opcodes.ICONST_0 -> produceIconst(0);
			case Opcodes.ICONST_1 -> produceIconst(1);
			case Opcodes.ICONST_2 -> produceIconst(2);
			case Opcodes.ICONST_3 -> produceIconst(3);
			case Opcodes.ICONST_4 -> produceIconst(4);
			case Opcodes.ICONST_5 -> produceIconst(5);
			case Opcodes.DUP -> {
				if (stack.isEmpty())
					System.err.println("Stack is empty");
				stack.push(stack.peek());
			}
			default -> throw new RuntimeException(String.format("Unsupported Insn opcode 0x%X", node.getOpcode()));
		}
	}
	
	private void produceIconst(int n)
	{
		stack.push(new PrimitiveType(PType.INTEGER, n));
	}
	
	private void processIntInsn(IntInsnNode node)
	{
		switch (node.getOpcode())
		{
			case Opcodes.BIPUSH -> stack.push(new PrimitiveType(PrimitiveType.PType.INTEGER, node.operand));
			default -> throw new RuntimeException(String.format("Unsupported IntInsn opcode 0x%X %d", node.getOpcode(), node.operand));
		}
	}

	private void preprocessJumpInsn(JumpInsnNode node)
	{
		var state = (LabelState) node.label.getLabel().info;
		state.isUsed = true;
		if (state.id == -1)
			state.id = labelCount++;
	}
	
	private void processJumpInsn(JumpInsnNode node)
	{
		switch (node.getOpcode())
		{
			case Opcodes.IF_ICMPEQ -> produceBiConditionalBranch("==", node);
			case Opcodes.IF_ICMPNE -> produceBiConditionalBranch("!=", node);
			case Opcodes.IF_ICMPLT -> produceBiConditionalBranch("<", node);
			case Opcodes.IF_ICMPLE -> produceBiConditionalBranch("<=", node);
			case Opcodes.IF_ICMPGT -> produceBiConditionalBranch(">", node);
			case Opcodes.IF_ICMPGE -> produceBiConditionalBranch(">=", node);
			case Opcodes.IFEQ -> produceConditionalBranch("==", node);
			case Opcodes.IFNE -> produceConditionalBranch("!=", node);
			case Opcodes.IFLT -> produceConditionalBranch("<", node);
			case Opcodes.IFLE -> produceConditionalBranch("<=", node);
			case Opcodes.IFGT -> produceConditionalBranch(">", node);
			case Opcodes.IFGE -> produceConditionalBranch(">=", node);
			default -> throw new RuntimeException(String.format("Unsupported JumpInsn opcode 0x%X %s", node.getOpcode(), getLabel(node.label)));
		}
	}
	
	private void processFieldInsn(FieldInsnNode node)
	{
		switch (node.getOpcode())
		{
			//default -> System.err.printf("WARNING: FieldInsnNode (0x%X) is not yet supported%n", node.getOpcode());
			case Opcodes.PUTFIELD -> {
				var value = stack.pop();
				var ref = stack.pop();
				printfln("%s->%s = %s;", ref.asValue(), node.name, value.asValue());
			}
			case Opcodes.GETFIELD -> {
				var ref = stack.pop();
				var command = String.format("(%s->%s)", ref.asValue(), node.name);
				stack.push(new VariableType(command));
			}
			default -> throw new RuntimeException(String.format("Unsupported FieldInsnNode opcode 0x%X", node.getOpcode()));
		}
	}
	
	private void processTypeInsn(TypeInsnNode node)
	{
		switch (node.getOpcode())
		{
			case Opcodes.NEW -> {
				var targetClass = new DecompiledClass(null, node.desc);
				var exp = String.format("(new %s())", targetClass.mangleName());
				stack.push(new VariableType(exp));
			}
			default -> throw new RuntimeException(String.format("Unsupported TypeInsnNode opcode 0x%X %s", node.getOpcode(), node.desc)); 
		}
	}
	
	private void processLdcInsn(LdcInsnNode node)
	{
		if (node.cst instanceof String)
			stack.push(new StringType((String) node.cst));
		else
			throw new RuntimeException(String.format("Unsupported LDC instruction of type '%s'", node.cst.getClass().getSimpleName()));
	}
	
	private void produceConditionalBranch(String condition, JumpInsnNode node)
	{
		printfln("if (%s %s 0) goto %s;", stack.pop().asValue(), condition, getLabel(node.label));
	}
	
	private void produceBiConditionalBranch(String condition, JumpInsnNode node)
	{
		var b = stack.pop().asValue();
		var a = stack.pop().asValue();
		printfln("if (%s %s %s) goto %s;", a, condition, b, getLabel(node.label));
	}
	
	private void invocationToJXEInternal(MethodInsnNode node, DecompiledMethod target)
	{
		switch (target.getName())
		{
			case "code" -> {
				var string = (StringType) stack.pop();
				stack.push(new VariableType(string.getString()));
			}
			default -> throw new UnsupportedOperationException("JXE." + target.getName() + " is not supported");
		}
	}
	
	@Override
	protected LabelNode getLabelNode(Label label)
	{
		var node = super.getLabelNode(label);
		node.getLabel().info = new LabelState();
		return node;
	}
	
	private String getVariable(int i)
	{
		if (method.isStaticMethod())
			return "v" + i;
		if (i == 0)
			return "this";
		return "v" + (i - 1);
	}
	
	private void printfln(String fmt, Object... args)
	{
		out.print(INDENT);
		out.printf(fmt, args);
		out.println();
	}
	
	private void println(String line)
	{
		out.print(INDENT);
		out.println(line);
	}
	
	private void binaryExpression(String operand)
	{
		var b = stack.pop().asValue();
		var a = stack.pop().asValue();
		stack.push(new VariableType(String.format("(%s %s %s)", a, operand, b))); 
	}
	
	private String getLabel(Label label)
	{
		//return Integer.toString((Integer) label.info);
		return String.format("lbl%d", ((LabelState) label.info).id);
	}
	
	private String getLabel(LabelNode label)
	{
		return getLabel(label.getLabel());
	}
	
	/*private void addInstruction(Runnable runnable)
	{
		instructionGenerators.add(runnable);
	}*/
}

class LabelState
{
	public boolean isUsed = false;
	public int id = -1;
}
















