import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class Transaction implements Serializable {
    private String id;
    private LocalDate date;
    private String description;
    private String category;
    private double amount;
    private TransactionType type;
    private String username; // Link to user

    public enum TransactionType {
        INCOME, EXPENSE
    }

    public Transaction(String username, LocalDate date, String description, 
                      String category, double amount, TransactionType type) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.date = date;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.type = type;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public TransactionType getType() { return type; }

    @Override
    public String toString() {
        return String.format("%s | %s | %-15s | %-20s | $%10.2f | %s",
                date, type, category, description, amount, id.substring(0, 8));
    }
}