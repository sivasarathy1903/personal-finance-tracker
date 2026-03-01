import java.io.Serializable;
import java.time.YearMonth;

public class Budget implements Serializable {
    private String username;
    private String category;
    private double amount;
    private YearMonth month;
    private double spent;

    public Budget(String username, String category, double amount, YearMonth month) {
        this.username = username;
        this.category = category;
        this.amount = amount;
        this.month = month;
        this.spent = 0.0;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public YearMonth getMonth() { return month; }
    public double getSpent() { return spent; }
    public void setSpent(double spent) { this.spent = spent; }

    public double getRemaining() {
        return amount - spent;
    }

    public double getUsagePercentage() {
        if (amount == 0) return 0;
        return (spent / amount) * 100;
    }

    public boolean isExceeded() {
        return spent > amount;
    }

    @Override
    public String toString() {
        return String.format("Budget: %s | Limit: $%.2f | Spent: $%.2f | Remaining: $%.2f | Usage: %.1f%%",
                category, amount, spent, getRemaining(), getUsagePercentage());
    }
}