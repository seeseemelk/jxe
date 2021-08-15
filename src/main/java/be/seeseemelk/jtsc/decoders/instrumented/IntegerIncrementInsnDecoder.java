package be.seeseemelk.jtsc.decoders.instrumented;

import be.seeseemelk.jtsc.recompiler.InstrumentedMethod;
import be.seeseemelk.jtsc.recompiler.SourceWriter;

public class IntegerIncrementInsnDecoder
{
	public static void visit(
			InstrumentedMethod method,
			SourceWriter writer,
			int var,
			int count
	)
	{
		String variable = method.getVar(var);
		if (count > 0)
			writer.writelnUnsafe(variable," += ",count,";");
		else
			writer.writelnUnsafe(variable," -= ",-count,";");
	}
}
