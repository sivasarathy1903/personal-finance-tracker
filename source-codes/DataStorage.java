import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {
    private static final String USERS_DIR = "data/users/";
    private static final String TRANSACTIONS_DIR = "data/transactions/";
    private static final String BUDGETS_DIR = "data/budgets/";
    private static final String GOALS_DIR = "data/goals/";
    private static final String REPORTS_DIR = "data/reports/";
    private static final String NOTIFICATIONS_DIR = "data/notifications/";

    static {
        try {
            // Create directories if they don't exist
            createDirectory(USERS_DIR);
            createDirectory(TRANSACTIONS_DIR);
            createDirectory(BUDGETS_DIR);
            createDirectory(GOALS_DIR);
            createDirectory(REPORTS_DIR);
            createDirectory(NOTIFICATIONS_DIR);
        } catch (Exception e) {
            System.err.println("Critical error creating directories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createDirectory(String path) {
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    System.err.println("Warning: Could not create directory: " + path);
                }
            }
        } catch (SecurityException e) {
            System.err.println("Security error creating directory " + path + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error creating directory " + path + ": " + e.getMessage());
        }
    }

    // User methods
    public static void saveUser(User user) {
        ObjectOutputStream oos = null;
        try {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null");
            }
            
            File file = new File(USERS_DIR + user.getUsername() + ".dat");
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(user);
            oos.flush();
            
            // Also create a human-readable text file
            saveUserDetailsToTextFile(user);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error saving user: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println("File not found error saving user: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error saving user: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error saving user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    System.err.println("Error closing output stream: " + e.getMessage());
                }
            }
        }
    }

    private static void saveUserDetailsToTextFile(User user) {
        PrintWriter writer = null;
        try {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null");
            }
            
            String filename = USERS_DIR + user.getUsername() + "_profile.txt";
            writer = new PrintWriter(new FileWriter(filename));
            writer.println("=".repeat(60));
            writer.println("USER PROFILE");
            writer.println("=".repeat(60));
            writer.println("Username: " + user.getUsername());
            writer.println("Email: " + user.getEmail());
            writer.println("Monthly Income: $" + String.format("%.2f", user.getMonthlyIncome()));
            writer.println("Account Created: " + user.getCreatedAt());
            writer.println("=".repeat(60));
            writer.flush();
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error saving user text file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error saving user text file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error saving user text file: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static User loadUser(String username) {
        ObjectInputStream ois = null;
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty");
            }
            
            File file = new File(USERS_DIR + username + ".dat");
            if (!file.exists()) {
                System.out.println("User file not found for: " + username);
                return null;
            }

            ois = new ObjectInputStream(new FileInputStream(file));
            return (User) ois.readObject();
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error loading user: " + e.getMessage());
            return null;
        } catch (FileNotFoundException e) {
            System.err.println("User file not found: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("IO error loading user: " + e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found error loading user: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error loading user: " + e.getMessage());
            return null;
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    System.err.println("Error closing input stream: " + e.getMessage());
                }
            }
        }
    }

    // Transaction methods
    public static void saveTransaction(Transaction transaction) {
        ObjectOutputStream oos = null;
        try {
            if (transaction == null) {
                throw new IllegalArgumentException("Transaction cannot be null");
            }
            
            File file = new File(TRANSACTIONS_DIR + transaction.getUsername() + 
                               "_" + transaction.getId() + ".dat");
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(transaction);
            oos.flush();
            
            // Append to user's transaction log
            appendTransactionToLog(transaction);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error saving transaction: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error saving transaction: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error saving transaction: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    System.err.println("Error closing output stream: " + e.getMessage());
                }
            }
        }
    }

    private static void appendTransactionToLog(Transaction transaction) {
        PrintWriter writer = null;
        try {
            if (transaction == null) {
                throw new IllegalArgumentException("Transaction cannot be null");
            }
            
            String filename = TRANSACTIONS_DIR + transaction.getUsername() + "_transactions_log.txt";
            File file = new File(filename);
            writer = new PrintWriter(new FileWriter(filename, true));
            
            if (file.length() == 0) {
                writer.println("=".repeat(100));
                writer.println("TRANSACTION LOG FOR USER: " + transaction.getUsername());
                writer.println("=".repeat(100));
                writer.printf("%-12s | %-10s | %-15s | %-25s | %-12s | %-10s%n", 
                    "DATE", "TYPE", "CATEGORY", "DESCRIPTION", "AMOUNT", "ID");
                writer.println("-".repeat(100));
            }
            writer.printf("%-12s | %-10s | %-15s | %-25s | $%-11.2f | %s%n",
                transaction.getDate(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getId().substring(0, 8));
            writer.flush();
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error appending transaction: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error appending transaction to log: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error appending transaction: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static List<Transaction> loadTransactions(String username) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty");
            }
            
            File dir = new File(TRANSACTIONS_DIR);
            if (!dir.exists()) {
                System.out.println("Transactions directory not found");
                return transactions;
            }
            
            File[] files = dir.listFiles((d, name) -> name.startsWith(username + "_") && name.endsWith(".dat"));

            if (files != null) {
                for (File file : files) {
                    ObjectInputStream ois = null;
                    try {
                        ois = new ObjectInputStream(new FileInputStream(file));
                        transactions.add((Transaction) ois.readObject());
                    } catch (IOException e) {
                        System.err.println("Error loading transaction file " + file.getName() + ": " + e.getMessage());
                    } catch (ClassNotFoundException e) {
                        System.err.println("Class not found error for file " + file.getName() + ": " + e.getMessage());
                    } finally {
                        if (ois != null) {
                            try {
                                ois.close();
                            } catch (IOException e) {
                                System.err.println("Error closing stream: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error loading transactions: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error loading transactions: " + e.getMessage());
        }
        return transactions;
    }

    public static void deleteTransaction(String transactionId) {
        try {
            if (transactionId == null || transactionId.trim().isEmpty()) {
                throw new IllegalArgumentException("Transaction ID cannot be null or empty");
            }
            
            File dir = new File(TRANSACTIONS_DIR);
            if (!dir.exists()) {
                System.out.println("Transactions directory not found");
                return;
            }
            
            File[] files = dir.listFiles((d, name) -> name.contains(transactionId));
            
            if (files != null) {
                for (File file : files) {
                    try {
                        boolean deleted = file.delete();
                        if (!deleted) {
                            System.err.println("Could not delete file: " + file.getName());
                        }
                    } catch (SecurityException e) {
                        System.err.println("Security error deleting file " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error deleting transaction: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error deleting transaction: " + e.getMessage());
        }
    }

    // Budget methods
    public static void saveBudget(Budget budget) {
        ObjectOutputStream oos = null;
        try {
            if (budget == null) {
                throw new IllegalArgumentException("Budget cannot be null");
            }
            
            File file = new File(BUDGETS_DIR + budget.getUsername() + 
                               "_" + budget.getCategory() + 
                               "_" + budget.getMonth() + ".dat");
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(budget);
            oos.flush();
            
            // Save to text file
            saveBudgetToTextFile(budget);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error saving budget: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error saving budget: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error saving budget: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    System.err.println("Error closing output stream: " + e.getMessage());
                }
            }
        }
    }

    private static void saveBudgetToTextFile(Budget budget) {
        PrintWriter writer = null;
        try {
            if (budget == null) {
                throw new IllegalArgumentException("Budget cannot be null");
            }
            
            String filename = BUDGETS_DIR + budget.getUsername() + "_budgets.txt";
            File file = new File(filename);
            writer = new PrintWriter(new FileWriter(filename, true));
            
            if (file.length() == 0) {
                writer.println("=".repeat(80));
                writer.println("BUDGET TRACKER FOR USER: " + budget.getUsername());
                writer.println("=".repeat(80));
            }
            writer.printf("%nCategory: %s | Month: %s%n", budget.getCategory(), budget.getMonth());
            writer.printf("Budget Limit: $%.2f%n", budget.getAmount());
            writer.printf("Amount Spent: $%.2f%n", budget.getSpent());
            writer.printf("Remaining: $%.2f%n", budget.getRemaining());
            writer.printf("Usage: %.1f%%%n", budget.getUsagePercentage());
            writer.printf("Status: %s%n", budget.isExceeded() ? "EXCEEDED" : "OK");
            writer.println("-".repeat(80));
            writer.flush();
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error saving budget text file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error saving budget text file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error saving budget text file: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static List<Budget> loadBudgets(String username) {
        List<Budget> budgets = new ArrayList<>();
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty");
            }
            
            File dir = new File(BUDGETS_DIR);
            if (!dir.exists()) {
                System.out.println("Budgets directory not found");
                return budgets;
            }
            
            File[] files = dir.listFiles((d, name) -> name.startsWith(username + "_") && name.endsWith(".dat"));

            if (files != null) {
                for (File file : files) {
                    ObjectInputStream ois = null;
                    try {
                        ois = new ObjectInputStream(new FileInputStream(file));
                        budgets.add((Budget) ois.readObject());
                    } catch (IOException e) {
                        System.err.println("Error loading budget file " + file.getName() + ": " + e.getMessage());
                    } catch (ClassNotFoundException e) {
                        System.err.println("Class not found error for file " + file.getName() + ": " + e.getMessage());
                    } finally {
                        if (ois != null) {
                            try {
                                ois.close();
                            } catch (IOException e) {
                                System.err.println("Error closing stream: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error loading budgets: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error loading budgets: " + e.getMessage());
        }
        return budgets;
    }

    // Financial Goal methods
    public static void saveFinancialGoal(FinancialGoal goal) {
        ObjectOutputStream oos = null;
        try {
            if (goal == null) {
                throw new IllegalArgumentException("Financial goal cannot be null");
            }
            
            File file = new File(GOALS_DIR + goal.getUsername() + "_" + goal.getId() + ".dat");
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(goal);
            oos.flush();
            
            // Save to text file
            saveGoalToTextFile(goal);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error saving financial goal: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error saving financial goal: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error saving financial goal: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    System.err.println("Error closing output stream: " + e.getMessage());
                }
            }
        }
    }

    private static void saveGoalToTextFile(FinancialGoal goal) {
        PrintWriter writer = null;
        try {
            if (goal == null) {
                throw new IllegalArgumentException("Financial goal cannot be null");
            }
            
            String filename = GOALS_DIR + goal.getUsername() + "_goals.txt";
            File file = new File(filename);
            writer = new PrintWriter(new FileWriter(filename, true));
            
            if (file.length() == 0) {
                writer.println("=".repeat(80));
                writer.println("FINANCIAL GOALS FOR USER: " + goal.getUsername());
                writer.println("=".repeat(80));
            }
            writer.printf("%nGoal: %s%n", goal.getName());
            writer.printf("Description: %s%n", goal.getDescription());
            writer.printf("Target Amount: $%.2f%n", goal.getTargetAmount());
            writer.printf("Current Amount: $%.2f%n", goal.getCurrentAmount());
            writer.printf("Progress: %.1f%%%n", goal.getProgressPercentage());
            writer.printf("Target Date: %s%n", goal.getTargetDate());
            writer.printf("Days Remaining: %d%n", goal.getDaysRemaining());
            writer.printf("Status: %s%n", goal.isCompleted() ? "COMPLETED" : 
                         goal.isOverdue() ? "OVERDUE" : "IN PROGRESS");
            writer.println("-".repeat(80));
            writer.flush();
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error saving goal text file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error saving goal text file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error saving goal text file: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static List<FinancialGoal> loadFinancialGoals(String username) {
        List<FinancialGoal> goals = new ArrayList<>();
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty");
            }
            
            File dir = new File(GOALS_DIR);
            if (!dir.exists()) {
                System.out.println("Goals directory not found");
                return goals;
            }
            
            File[] files = dir.listFiles((d, name) -> name.startsWith(username + "_") && name.endsWith(".dat"));

            if (files != null) {
                for (File file : files) {
                    ObjectInputStream ois = null;
                    try {
                        ois = new ObjectInputStream(new FileInputStream(file));
                        goals.add((FinancialGoal) ois.readObject());
                    } catch (IOException e) {
                        System.err.println("Error loading goal file " + file.getName() + ": " + e.getMessage());
                    } catch (ClassNotFoundException e) {
                        System.err.println("Class not found error for file " + file.getName() + ": " + e.getMessage());
                    } finally {
                        if (ois != null) {
                            try {
                                ois.close();
                            } catch (IOException e) {
                                System.err.println("Error closing stream: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error loading goals: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error loading goals: " + e.getMessage());
        }
        return goals;
    }

    // Notification methods
    public static void saveNotification(String username, String notification) {
        PrintWriter writer = null;
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty");
            }
            if (notification == null || notification.trim().isEmpty()) {
                throw new IllegalArgumentException("Notification cannot be null or empty");
            }
            
            String filename = NOTIFICATIONS_DIR + username + "_notifications.txt";
            writer = new PrintWriter(new FileWriter(filename, true));
            writer.printf("[%s] %s%n", java.time.LocalDateTime.now(), notification);
            writer.flush();
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error saving notification: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error saving notification: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error saving notification: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static List<String> loadNotifications(String username) {
        List<String> notifications = new ArrayList<>();
        BufferedReader reader = null;
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty");
            }
            
            String filename = NOTIFICATIONS_DIR + username + "_notifications.txt";
            File file = new File(filename);
            
            if (!file.exists()) {
                return notifications;
            }
            
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                notifications.add(line);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error loading notifications: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error loading notifications: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error loading notifications: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Error closing reader: " + e.getMessage());
                }
            }
        }
        return notifications;
    }

    public static void clearNotifications(String username) {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty");
            }
            
            String filename = NOTIFICATIONS_DIR + username + "_notifications.txt";
            File file = new File(filename);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.err.println("Could not delete notifications file");
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error clearing notifications: " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("Security error clearing notifications: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error clearing notifications: " + e.getMessage());
        }
    }

    // Generate comprehensive report
    public static void generateUserReport(String username, String reportContent) {
        PrintWriter writer = null;
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty");
            }
            if (reportContent == null) {
                throw new IllegalArgumentException("Report content cannot be null");
            }
            
            String filename = REPORTS_DIR + username + "_report_" + 
                             java.time.LocalDateTime.now().format(
                                 java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
            writer = new PrintWriter(new FileWriter(filename));
            writer.println("=".repeat(100));
            writer.println("COMPREHENSIVE FINANCIAL REPORT");
            writer.println("User: " + username);
            writer.println("Generated: " + java.time.LocalDateTime.now());
            writer.println("=".repeat(100));
            writer.println();
            writer.println(reportContent);
            writer.println();
            writer.println("=".repeat(100));
            writer.println("END OF REPORT");
            writer.println("=".repeat(100));
            writer.flush();
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error generating report: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error generating report: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error generating report: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}