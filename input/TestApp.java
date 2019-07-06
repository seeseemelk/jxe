package testapp;

final public class TestApp
{
	//private static int myProperty = 2;
	
	public static int someFunc()
	{
		int a = 5 + 5;
		int b = 6 + 6;
		return a * b;
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

}
