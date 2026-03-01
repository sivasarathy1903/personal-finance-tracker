import java.io.Serializable;
import java.time.LocalDate;

public class FinancialGoal implements Serializable {
    private String id;
    private String username;
    private String name;
    private String description;
    private double targetAmount;
    private double currentAmount;
    private LocalDate targetDate;
    private LocalDate createdAt;

    public FinancialGoal(String username, String name, String description, 
                        double targetAmount, LocalDate targetDate) {
        this.id = java.util.UUID.randomUUID().toString();
        this.username = username;
        this.name = name;
        this.description = description;
        this.targetAmount = targetAmount;
        this.currentAmount = 0.0;
        this.targetDate = targetDate;
        this.createdAt = LocalDate.now();
    }

    // Getters and setters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getTargetAmount() { return targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public LocalDate getTargetDate() { return targetDate; }
    public LocalDate getCreatedAt() { return createdAt; }

    public void setCurrentAmount(double amount) { this.currentAmount = amount; }
    public void addAmount(double amount) { this.currentAmount += amount; }

    public double getProgressPercentage() {
        return (currentAmount / targetAmount) * 100;
    }

    public long getDaysRemaining() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
    }

    public boolean isCompleted() {
        return currentAmount >= targetAmount;
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(targetDate) && !isCompleted();
    }

    @Override
    public String toString() {
        String status = isCompleted() ? "COMPLETED" : 
                       isOverdue() ? "OVERDUE" : "IN PROGRESS";
        return String.format("Goal: %s | Progress: $%.2f/$%.2f (%.1f%%) | Due: %s (%d days) | %s",
                name, currentAmount, targetAmount, getProgressPercentage(), 
                targetDate, getDaysRemaining(), status);
    }
}