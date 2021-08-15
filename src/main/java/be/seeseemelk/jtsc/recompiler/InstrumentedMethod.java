package be.seeseemelk.jtsc.recompiler;

public class InstrumentedMethod
{
	private final MethodDescriptor descriptor;
	private final InstructionStream stream;

	public InstrumentedMethod(MethodDescriptor descriptor, InstructionStream stream)
	{
		this.descriptor = descriptor;
		this.stream = stream;
	}

	public MethodDescriptor getDescriptor()
	{
		return descriptor;
	}

	public InstructionStream getStream()
	{
		return stream;
	}

	public int argOffset()
	{
		if (descriptor.isStatic())
			return 0;
		else
			return 1;
	}

	public boolean isThis(int index)
	{
		if (descriptor.isStatic())
			return false;
		else if (index > 0)
			return false;
		else
			return true;
	}

	public boolean isArgument(int index)
	{
		if (isThis(index))
			return false;
		else if (index < argOffset())
			return false;
		else if (index < descriptor.getArguments().size())
			return true;
		else
			return false;
	}

	public boolean isLocal(int index)
	{
		return !isArgument(index);
	}

	public String getVar(int index)
	{
		if (isThis(index))
			return "this";
		else if (isArgument(index))
			return "arg" + (index - argOffset());
		else
			return "var" + (index - descriptor.getArguments().size());
	}

	public String accessVar(int index, String as)
	{
		if (isThis(index))
			return "this";
		else if (isArgument(index))
			return getVar(index);
		else
			return getVar(index) + "." + as;
	}
}
