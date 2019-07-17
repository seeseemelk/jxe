package be.seeseemelk.jtsc;

import org.objectweb.asm.Opcodes;

public enum Accessor
{
	PUBLIC,
	PROTECTED,
	PRIVATE,
	UNSPECIFIED;
	
	public static Accessor fromAccessInt(int access)
	{
		return switch (access & (Opcodes.ACC_PUBLIC  | Opcodes.ACC_PROTECTED | Opcodes.ACC_PRIVATE)) {
			case Opcodes.ACC_PUBLIC -> PUBLIC;
			case Opcodes.ACC_PROTECTED -> PROTECTED;
			case Opcodes.ACC_PRIVATE -> PRIVATE;
			default -> throw new IllegalArgumentException("Unsupported access type: " + access);
		};
	}
}
