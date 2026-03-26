package refactoring.examples.TestBank.ExtractMethod;

public class Order {
    private final String id;
    private final double amount;
    private final boolean valid;

    public Order(String id, double amount, boolean valid) {
        this.id = id;
        this.amount = amount;
        this.valid = valid;
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isValid() {
        return valid;
    }
}
