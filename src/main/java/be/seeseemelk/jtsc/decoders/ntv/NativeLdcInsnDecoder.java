package be.seeseemelk.jtsc.decoders.ntv;

import org.objectweb.asm.Type;

import be.seeseemelk.jtsc.recompiler.MethodState;
import be.seeseemelk.jtsc.recompiler.Utils;

public final class NativeLdcInsnDecoder
{
	private NativeLdcInsnDecoder() {}
	
	public static void visit(MethodState state, Object value)
	{
		try
		{
			if (value instanceof String)
				state.pushToStack("new String(\"" + value + "\")");
			else if (value instanceof Double)
				state.pushToStack(value.toString());
			else if (value instanceof Integer)
				state.pushToStack(value.toString());
			else if (value instanceof Float)
				state.pushToStack(value.toString() + "f");
			else if (value instanceof Type)
				state.pushToStack(Utils.typeToName(((Type) value).toString()) + "._class");
			else
				throw new UnsupportedOperationException("Unknown constant: (" + value.getClass().getSimpleName() + ") " + value);
		}
		catch (Exception e)
		{
			throw new RuntimeException(String.format(
					"Exception occured while processing 0x%X[value=%s]",
					value == null ? "null" : value.toString()),
					e);
		}
	}
}
