package refactoring.examples.TestBank.FormTemplateMethod;

public class TextStatement extends Statement {
	public String value() {
		String result = headerString();
		result += eachRentalString();
		result += footerString();
		return result;
	}

	String headerString() {
		return "Text Header\n";
	}

	String eachRentalString() {
		return "Text Rental Line\n";
	}

	String footerString() {
		return "Text Footer\n";
	}
}
