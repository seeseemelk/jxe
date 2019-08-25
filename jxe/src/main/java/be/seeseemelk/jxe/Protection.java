package be.seeseemelk.jxe;

import org.objectweb.asm.Opcodes;

public enum Protection
{
	PUBLIC,
	PROTECTED,
	PRIVATE,
	PACKAGE,
	UNSPECIFIED;
	
	public static Protection fromProtectionInt(int access)
	{
		return switch (access & (Opcodes.ACC_PUBLIC  | Opcodes.ACC_PROTECTED | Opcodes.ACC_PRIVATE)) {
			case 0 -> PACKAGE;
			case Opcodes.ACC_PUBLIC -> PUBLIC;
			case Opcodes.ACC_PROTECTED -> PROTECTED;
			case Opcodes.ACC_PRIVATE -> PRIVATE;
			default -> throw new IllegalArgumentException("Unsupported access type: " + access);
		};
	}
	
	public static boolean isStatic(int access)
	{
		return (access & Opcodes.ACC_STATIC) > 0;
	}
}
