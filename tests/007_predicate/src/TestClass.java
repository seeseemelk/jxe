import java.util.function.Predicate;

public class TestClass
{
	public Predicate<Integer> getPositivePredicate()
	{
		return n -> n > 0;

		/**return new Predicate<Integer>()
		{
				@Override
				public boolean test(Integer n)
				{
					return n > 0;
				}
		};*/
	}

	public boolean isPositive(int n)
	{
		return getPositivePredicate().test(n);
	}
}
