package be.seeseemelk.jtsc.recompiler;

import java.util.ArrayList;
import java.util.List;

public class BaseWriter
{
	private final SourceWriter writer;
	private final MethodDescriptor descriptor;

	public BaseWriter(SourceWriter writer, MethodDescriptor descriptor)
	{
		this.writer = writer;
		this.descriptor = descriptor;
	}

	public void writePrelude()
	{
		List<String> keywords = new ArrayList<>();

		if (!descriptor.isStaticInitializer())
		{
			switch (descriptor.getVisibility())
			{
				case PRIVATE:
					keywords.add("private ");
					break;
				case PACKAGE:
					System.err.println("Warning: PACKAGE visibility is not supported, using PUBLIC instead");
				case PUBLIC:
					keywords.add("public ");
					break;
				case PROTECTED:
					keywords.add("protected ");
					break;
			}
		}

		if (descriptor.isStatic() || descriptor.isStaticInitializer())
			keywords.add("static ");

		if (descriptor.isConstructor() || descriptor.isStaticInitializer())
		{
			keywords.add("override void");
			keywords.add(" __construct");
		}
		else
		{
			keywords.add(descriptor.getReturnType());
			keywords.add(" ");
			keywords.add(descriptor.getName());
		}

		keywords.add("(");

		int argCount = 0;
		// Non-static functions have a 'this' parameter at index 0.
		for (var arg : descriptor.getArguments())
		{
			keywords.add(Utils.getClassName(arg));
			keywords.add(" ");
			keywords.add("arg" + (argCount));
			argCount++;
			if (argCount < descriptor.getArguments().size())
				keywords.add(", ");
		}

		keywords.add(") ");
		keywords.add("{");

		writer.writelnUnsafe(String.join("", keywords));
		writer.indent();
	}

	public void writePostlude()
	{
		writer.undent();
		writer.writelnUnsafe("}");
		writer.writelnUnsafe();
	}
}
