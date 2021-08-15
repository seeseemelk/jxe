// Class name: TestClass
// Version: 60
// Access: 33
// Super Name: java/lang/Object
import java.lang.Object;
import java.lang.String : String;

public class TestClass : _Object {
	mixin autoReflector!TestClass;
	
	private this() {
		super();
	}
	
	static TestClass __new(T...)(T t) {
		auto obj = new TestClass;
		obj.__construct(t);
		return obj;
	}
	
	public override void __construct() {
		import java.lang.instrumentation;
		JavaVar[] vars;
		size_t varsTop = 0;
		size_t address = 0;
		for (;;) {
			switch (address) {
			case 0:
				vars ~= JavaVar.ofObject(this);
				varsTop++;
				address++;
				break;
			case 1:
				super.__construct();
				address++;
				break;
			case 2:
				return;
			default:
				assert(0, "Invalid address");
			}
		}
	}
	
}
