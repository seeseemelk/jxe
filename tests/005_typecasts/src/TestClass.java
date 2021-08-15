public class TestClass
{
	public static float castIntToFloat(int t)
	{
		return (float) t;
	}
	
	public static B castAToB(A a)
	{
		return (B) a;
	}
	
	public static A castBToA(B b)
	{
		return (A) b;
	}
	
	public static boolean isA(Object obj)
	{
		return obj instanceof A;
	}
}