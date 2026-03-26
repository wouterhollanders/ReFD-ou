package refactoring.examples.TestBank;

import java.util.Arrays;
import java.util.List;

import refactoring.examples.TestBank.ExtractMethod.BillingPrinterA;
import refactoring.examples.TestBank.ExtractMethod.LocalVariableExtraction;
import refactoring.examples.TestBank.ExtractMethod.Order;

public class App {

	// Used to start the application
	public static void main(String[] args) {

		// Arrange: a sequence where break matters
		List<Order> orders = Arrays.asList(new Order("A", 200, true), // valid, added
				new Order("B", 1500, true), // valid, triggers break
				new Order("C", 300, true) // would be added if break is mishandled
		);
		
		double previousAmount = 100.0;

		BillingPrinterA a = new BillingPrinterA();
		a.printOwing(orders, previousAmount);
		
		LocalVariableExtraction.Test();
	}
}
