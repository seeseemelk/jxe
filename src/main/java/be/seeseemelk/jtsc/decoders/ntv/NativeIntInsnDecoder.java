package be.seeseemelk.jtsc.decoders.ntv;

import org.objectweb.asm.Opcodes;

import be.seeseemelk.jtsc.instructions.IntInstruction;
import be.seeseemelk.jtsc.recompiler.MethodState;

public class NativeIntInsnDecoder
{
	private static void visitBiPush(MethodState state, int value)
	{
		state.pushToStack(Integer.toString(value));
	}

	public static void visit(MethodState state, IntInstruction instruction)
	{
		var opcode = instruction.getOpcode();
		var value = instruction.getValue();
		switch (instruction.getOpcode())
		{
			case Opcodes.BIPUSH:
				visitBiPush(state, value);
				break;
			default:
				throw new UnsupportedOperationException(
						"Unknown int instruction: " + opcode + ", " + value
				);
		}
	}

}
