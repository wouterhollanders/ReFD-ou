package refactoring.examples.TestBank.ExtractMethod;

import java.util.List;

public abstract class BillingBase {
	protected double outstanding;
	protected double percentage = 1.2;
	abstract void printOwing(List<Order> orders, double previousAmount);
}