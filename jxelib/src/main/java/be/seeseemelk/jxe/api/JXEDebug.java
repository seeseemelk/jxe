package be.seeseemelk.jxe.api;

@Include("stdio.h")
public final class JXEDebug
{
	private JXEDebug()
	{
		
	}
	
	public static void println(String str)
	{
		//JXE.var("const char* wow", str);
		JXE.code("puts(\"wow\")");
	}
}
