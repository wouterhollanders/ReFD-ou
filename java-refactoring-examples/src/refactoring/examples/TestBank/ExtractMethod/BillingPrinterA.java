package refactoring.examples.TestBank.ExtractMethod;

import java.util.List;

public class BillingPrinterA extends BillingBase {

	public void printOwing(List<Order> orders, double previousAmount) {
		double percentage = 1.4;
		int orderCount = 0;

		System.out.println("**************************");
		System.out.println("***** Customer ******");
		System.out.println("**************************");

		outstanding = previousAmount * percentage;

		for (Order order : orders) {

			if (!order.isValid())
				continue;

			orderCount++;

			if (order.getAmount() > 1000) {
				System.out.println("High value order: " + order.getId());
				break;
			}
			outstanding += order.getAmount();
		}

		System.out.println("A: amount: " + outstanding);
		System.out.println("A: valid orders counted: " + orderCount);
		System.out.println("**************************");
	}
}
