package refactoring.examples.TestBank.FormTemplateMethod;

//After
abstract class StatementA {
	public String value() { // Template Method
		String result = headerString();
		result += eachRentalString();
		result += footerString();
		return result;
	}

	abstract String headerString();

	abstract String eachRentalString();

	abstract String footerString();
}
