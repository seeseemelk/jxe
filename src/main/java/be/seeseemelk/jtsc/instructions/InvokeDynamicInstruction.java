package be.seeseemelk.jtsc.instructions;

import org.objectweb.asm.Handle;

public class InvokeDynamicInstruction extends Instruction
{
	private final String name;
	private final String descriptor;
	private final Handle bootstrapMethodHandle;
	private final Object[] bootstrapMethodArguments;

	public InvokeDynamicInstruction(
			String name,
			String descriptor,
			Handle bootstrapMethodHandle,
			Object... bootstrapMethodArguments)
	{
		this.name = name;
		this.descriptor = descriptor;
		this.bootstrapMethodHandle = bootstrapMethodHandle;
		this.bootstrapMethodArguments = bootstrapMethodArguments;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescriptor()
	{
		return descriptor;
	}
	
	public Handle getBootstrapMethodHandle()
	{
		return bootstrapMethodHandle;
	}
	
	public Object[] getBootstrapMethodArguments()
	{
		return bootstrapMethodArguments;
	}

}
