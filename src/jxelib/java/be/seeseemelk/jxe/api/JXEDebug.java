package be.seeseemelk.jxe.api;

@Include("iostream")
public final class JXEDebug
{
	private JXEDebug()
	{
		
	}
	
	public static void println(String str)
	{
		JXE.code("puts(\"" + str + "\")");
	}
}
