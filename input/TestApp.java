package be.seeseemelk.testapp;

public class TestApp
{
	//private static int myProperty = 2;
	public int a = 2;
	public int b = 3;
	private int c = 4;
	
	public static int someFunc()
	{
		int a = 5 + 5;
		int b = 6 + 6;
		return a * b;
	}
	
	public int myMethod()
	{
		return 2;
	}
	
	public final int myFinalMethod()
	{
		return 3;
	}
	
	public static int doubleValue(int n)
	{
		return n * 2;
	}
	
	public static int sum(int a, int b)
	{
		return a + b;
	}
	
	public static boolean isNegative(int n)
	{
		if (n < 0)
			return true;
		else
			return false;
	}
	
	public static int factorial(int n)
	{
		if (n == 1)
			return n;
		else
			return n * factorial(n - 1);
	}
	
	public static int createOne()
	{
		return new TestApp().a;
	}

}
