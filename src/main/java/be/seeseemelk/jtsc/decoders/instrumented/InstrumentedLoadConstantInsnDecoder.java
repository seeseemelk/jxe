package be.seeseemelk.jtsc.decoders.instrumented;

import be.seeseemelk.jtsc.recompiler.SourceWriter;

public class InstrumentedLoadConstantInsnDecoder
{
	public static void visit(SourceWriter writer, Object value)
	{
		writer.writelnUnsafe("vars ~= JavaVar.ofObject(new String(\"", value, "\"));");
	}
}
