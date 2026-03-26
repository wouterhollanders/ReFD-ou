package refactoring.examples.TestBank.ExtractMethod;

public class LocalVariableExtraction {
	private static int x;
	private static int y;
	private static int z;

	public static void Test() {
		ResetNumbers();
		m();
		ResetNumbers();
		e();
	}
	
	private static void ResetNumbers() {
		x = 0;
		y = 7;
		z = 0;
	}

	public static void m() {
		int ans = 0;
		if (x + y <= y + z) {
			ans = x + y;
		}
		while (x < y) {
			if (x + y <= y + z) {
				ans = x + y;
			}
			x = x + 1;
		}
		
		System.out.println("m(): ans = " + ans);

	}

	public static void e() {
		int ans = 0;
		extracted();
		while (x < y) {
			extracted();
			x = x + 1;
		}
		System.out.println("e(): ans = " + ans);
	}

	private static void extracted() {
		int ans;
		if (x + y <= y + z) {
			ans = x + y;
		}
	}
}
