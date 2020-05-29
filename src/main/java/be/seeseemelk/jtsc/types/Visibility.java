package be.seeseemelk.jtsc.types;

import org.objectweb.asm.Opcodes;

public enum Visibility
{
	PUBLIC,
	PACKAGE,
	PROTECTED,
	PRIVATE;
	
	/**
	 * Converts a ASM access integer to a `Visibility` enum.
	 * @param access
	 */
	public static Visibility fromAccess(int access)
	{
		if ((access & Opcodes.ACC_PUBLIC) != 0)
			return Visibility.PUBLIC;
		else if ((access & Opcodes.ACC_PROTECTED) != 0)
			return Visibility.PROTECTED;
		else if ((access & Opcodes.ACC_PRIVATE) != 0)
			return Visibility.PRIVATE;
		else
			return Visibility.PACKAGE;
	}
}
