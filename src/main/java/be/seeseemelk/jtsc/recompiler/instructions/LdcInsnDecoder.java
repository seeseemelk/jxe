package be.seeseemelk.jtsc.recompiler.instructions;

import org.objectweb.asm.Type;

import be.seeseemelk.jtsc.recompiler.MethodState;
import be.seeseemelk.jtsc.recompiler.Utils;

public final class LdcInsnDecoder
{
	private LdcInsnDecoder() {}
	
	public static void visit(MethodState state, Object value)
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
}
