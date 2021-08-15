public class TestClass
{
	public static int count(int f)
	{
		int count = 0;
		while (f != 0)
		{
			count += f;
			f--;
		}
		return count;
	}
}