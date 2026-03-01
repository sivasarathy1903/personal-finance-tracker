import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class FinanceManager {
    private List<Transaction> transactions;
    private List<Budget> budgets;
    private List<FinancialGoal> goals;
    private User currentUser;
    private List<String> notifications;

    public FinanceManager(User user) {
        this.currentUser = user;
        this.transactions = DataStorage.loadTransactions(user.getUsername());
        this.budgets = DataStorage.loadBudgets(user.getUsername());
        this.goals = DataStorage.loadFinancialGoals(user.getUsername());
        this.notifications = new ArrayList<>();
        checkForNotifications();
    }

    // Transaction methods
    public boolean addTransaction(LocalDate date, String description, String category, 
                                 double amount, Transaction.TransactionType type) {
        Transaction transaction = new Transaction(currentUser.getUsername(), date, 
                                                description, category, amount, type);
        transactions.add(transaction);
        updateBudgets(transaction);
        DataStorage.saveTransaction(transaction);
        
        // Check balance after adding transaction
        checkBalanceStatus();
        
        return true;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getTransactionsByCategory(String category) {
        return transactions.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByDateRange(LocalDate start, LocalDate end) {
        return transactions.stream()
                .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                .collect(Collectors.toList());
    }

    public boolean deleteTransaction(String transactionId) {
        Optional<Transaction> transaction = transactions.stream()
                .filter(t -> t.getId().equals(transactionId))
                .findFirst();
        
        if (transaction.isPresent()) {
            transactions.remove(transaction.get());
            DataStorage.deleteTransaction(transactionId);
            checkBalanceStatus();
            return true;
        }
        return false;
    }

    // Budget methods
    public boolean setBudget(String category, double amount, YearMonth month) {
        Budget budget = new Budget(currentUser.getUsername(), category, amount, month);
        budgets.add(budget);
        updateBudgetSpent(budget);
        DataStorage.saveBudget(budget);
        return true;
    }

    public List<Budget> getBudgets() {
        return new ArrayList<>(budgets);
    }

    public List<Budget> getCurrentMonthBudgets() {
        YearMonth currentMonth = YearMonth.now();
        return budgets.stream()
                .filter(b -> b.getMonth().equals(currentMonth))
                .collect(Collectors.toList());
    }

    // Financial Goal methods
    public boolean createFinancialGoal(String name, String description, 
                                     double targetAmount, LocalDate targetDate) {
        FinancialGoal goal = new FinancialGoal(currentUser.getUsername(), name, 
                                             description, targetAmount, targetDate);
        goals.add(goal);
        DataStorage.saveFinancialGoal(goal);
        return true;
    }

    public boolean updateGoalProgress(String goalId, double amount) {
        Optional<FinancialGoal> goal = goals.stream()
                .filter(g -> g.getId().equals(goalId))
                .findFirst();
        
        if (goal.isPresent()) {
            goal.get().addAmount(amount);
            DataStorage.saveFinancialGoal(goal.get());
            
            // Check if goal is completed
            if (goal.get().isCompleted()) {
                addNotification("Congratulations! You've completed your goal: " + goal.get().getName());
            }
            
            return true;
        }
        return false;
    }

    public List<FinancialGoal> getFinancialGoals() {
        return new ArrayList<>(goals);
    }

    // Analytics methods
    public double getCurrentBalance() {
        double income = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        double expenses = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        return income - expenses;
    }

    public Map<String, Double> getCategoryWiseSpending() {
        return transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                    Transaction::getCategory,
                    Collectors.summingDouble(Transaction::getAmount)
                ));
    }

    public double getMonthlySavingsRate() {
        double monthlyIncome = currentUser.getMonthlyIncome();
        if (monthlyIncome == 0) return 0;
        
        YearMonth currentMonth = YearMonth.now();
        LocalDate firstDay = currentMonth.atDay(1);
        LocalDate lastDay = currentMonth.atEndOfMonth();
        
        double monthlyExpenses = getTransactionsByDateRange(firstDay, lastDay).stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        return ((monthlyIncome - monthlyExpenses) / monthlyIncome) * 100;
    }

    public List<Budget> getExceededBudgets() {
        return budgets.stream()
                .filter(Budget::isExceeded)
                .collect(Collectors.toList());
    }

    // Notification methods
    public void checkForNotifications() {
        notifications.clear();
        
        // Check negative balance
        double balance = getCurrentBalance();
        if (balance < 0) {
            String notification = String.format("WARNING: Your balance is NEGATIVE! Current balance: -$%.2f", 
                                              Math.abs(balance));
            addNotification(notification);
        }
        
        // Check exceeded budgets
        List<Budget> exceededBudgets = getExceededBudgets();
        for (Budget budget : exceededBudgets) {
            String notification = String.format("BUDGET ALERT: %s budget exceeded by $%.2f", 
                                              budget.getCategory(), Math.abs(budget.getRemaining()));
            addNotification(notification);
        }
        
        // Check overdue goals
        List<FinancialGoal> overdueGoals = goals.stream()
                .filter(FinancialGoal::isOverdue)
                .collect(Collectors.toList());
        for (FinancialGoal goal : overdueGoals) {
            String notification = String.format("GOAL OVERDUE: %s - Still need $%.2f", 
                                              goal.getName(), 
                                              goal.getTargetAmount() - goal.getCurrentAmount());
            addNotification(notification);
        }
        
        // Check low balance warning (less than 10% of monthly income)
        if (balance > 0 && balance < (currentUser.getMonthlyIncome() * 0.1)) {
            String notification = String.format("LOW BALANCE WARNING: Only $%.2f remaining (less than 10%% of monthly income)", 
                                              balance);
            addNotification(notification);
        }
        
        // Check budgets near limit (>90%)
        List<Budget> nearLimitBudgets = getCurrentMonthBudgets().stream()
                .filter(b -> !b.isExceeded() && b.getUsagePercentage() > 90)
                .collect(Collectors.toList());
        for (Budget budget : nearLimitBudgets) {
            String notification = String.format("BUDGET WARNING: %s at %.1f%% - Only $%.2f remaining", 
                                              budget.getCategory(), 
                                              budget.getUsagePercentage(), 
                                              budget.getRemaining());
            addNotification(notification);
        }
    }

    private void addNotification(String message) {
        if (!notifications.contains(message)) {
            notifications.add(message);
            DataStorage.saveNotification(currentUser.getUsername(), message);
        }
    }

    public List<String> getNotifications() {
        return new ArrayList<>(notifications);
    }

    public void clearNotifications() {
        notifications.clear();
        DataStorage.clearNotifications(currentUser.getUsername());
    }

    private void checkBalanceStatus() {
        double balance = getCurrentBalance();
        if (balance < 0) {
            String notification = String.format("CRITICAL: Balance went negative! Current: -$%.2f", 
                                              Math.abs(balance));
            addNotification(notification);
        }
    }

    // Generate comprehensive report
    public String generateComprehensiveReport() {
        StringBuilder report = new StringBuilder();
        
        // User Summary
        report.append("USER SUMMARY\n");
        report.append("-".repeat(100)).append("\n");
        report.append(String.format("Username: %s%n", currentUser.getUsername()));
        report.append(String.format("Monthly Income: $%.2f%n", currentUser.getMonthlyIncome()));
        report.append(String.format("Current Balance: $%.2f%n", getCurrentBalance()));
        report.append("\n");
        
        // Transaction Summary
        report.append("TRANSACTION SUMMARY\n");
        report.append("-".repeat(100)).append("\n");
        report.append(String.format("Total Transactions: %d%n", transactions.size()));
        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount).sum();
        double totalExpenses = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount).sum();
        report.append(String.format("Total Income: $%.2f%n", totalIncome));
        report.append(String.format("Total Expenses: $%.2f%n", totalExpenses));
        report.append("\n");
        
        // Category-wise Spending
        report.append("CATEGORY-WISE SPENDING\n");
        report.append("-".repeat(100)).append("\n");
        Map<String, Double> categorySpending = getCategoryWiseSpending();
        categorySpending.forEach((cat, amt) -> 
            report.append(String.format("%-20s: $%.2f%n", cat, amt)));
        report.append("\n");
        
        // Budget Status
        report.append("BUDGET STATUS\n");
        report.append("-".repeat(100)).append("\n");
        List<Budget> currentBudgets = getCurrentMonthBudgets();
        if (currentBudgets.isEmpty()) {
            report.append("No budgets set for current month\n");
        } else {
            for (Budget b : currentBudgets) {
                report.append(String.format("%-15s: $%.2f / $%.2f (%.1f%%) - %s%n",
                    b.getCategory(), b.getSpent(), b.getAmount(), 
                    b.getUsagePercentage(), b.isExceeded() ? "EXCEEDED" : "OK"));
            }
        }
        report.append("\n");
        
        // Goals Status
        report.append("FINANCIAL GOALS\n");
        report.append("-".repeat(100)).append("\n");
        if (goals.isEmpty()) {
            report.append("No financial goals set\n");
        } else {
            for (FinancialGoal g : goals) {
                report.append(String.format("%-20s: $%.2f / $%.2f (%.1f%%) - %s%n",
                    g.getName(), g.getCurrentAmount(), g.getTargetAmount(),
                    g.getProgressPercentage(), 
                    g.isCompleted() ? "COMPLETED" : g.isOverdue() ? "OVERDUE" : "IN PROGRESS"));
            }
        }
        report.append("\n");
        
        // Recent Transactions
        report.append("RECENT TRANSACTIONS (Last 10)\n");
        report.append("-".repeat(100)).append("\n");
        List<Transaction> recentTransactions = transactions.stream()
                .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
                .limit(10)
                .collect(Collectors.toList());
        for (Transaction t : recentTransactions) {
            report.append(t.toString()).append("\n");
        }
        
        return report.toString();
    }

    public void saveComprehensiveReport() {
        String reportContent = generateComprehensiveReport();
        DataStorage.generateUserReport(currentUser.getUsername(), reportContent);
    }

    // Helper methods
    private void updateBudgets(Transaction transaction) {
        if (transaction.getType() == Transaction.TransactionType.EXPENSE) {
            YearMonth transactionMonth = YearMonth.from(transaction.getDate());
            budgets.stream()
                    .filter(b -> b.getCategory().equals(transaction.getCategory()) 
                              && b.getMonth().equals(transactionMonth))
                    .forEach(b -> {
                        b.setSpent(b.getSpent() + transaction.getAmount());
                        DataStorage.saveBudget(b);
                        
                        // Check if budget exceeded
                        if (b.isExceeded()) {
                            addNotification(String.format("Budget exceeded for %s: $%.2f over limit", 
                                          b.getCategory(), Math.abs(b.getRemaining())));
                        }
                    });
        }
    }

    private void updateBudgetSpent(Budget budget) {
        YearMonth month = budget.getMonth();
        LocalDate firstDay = month.atDay(1);
        LocalDate lastDay = month.atEndOfMonth();
        
        double spent = getTransactionsByDateRange(firstDay, lastDay).stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .filter(t -> t.getCategory().equals(budget.getCategory()))
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        budget.setSpent(spent);
    }
}