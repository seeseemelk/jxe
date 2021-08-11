package be.seeseemelk.jtsc.recompiler;

public final class NativeTreeWriter
{

	private NativeTreeWriter() {}

	public static void write(
		InstructionStream stream,
		MethodDescriptor methodDescriptor,
		SourceWriter writer
	)
	{
		var tree = InstructionTree.from(stream);
		var treeWriter = new TreeWriter(tree, writer, methodDescriptor);
		treeWriter.write();
	}
}
