import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.InputMismatchException;

public class MainApp {
    private static UserManager userManager;
    private static FinanceManager financeManager;
    private static Scanner scanner;
    private static boolean running = true;

    // ANSI Color Codes
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String RED = "\u001B[31m";
    private static final String BOLD = "\u001B[1m";
    private static final String DIM = "\u001B[2m";

    private static final int WIDTH = 80;

    public static void main(String[] args) {
        try {
            userManager = new UserManager();
            scanner = new Scanner(System.in);
            
            showWelcomeScreen();

            while (running) {
                try {
                    if (!userManager.isUserLoggedIn()) {
                        showAuthMenu();
                    } else {
                        showMainMenu();
                    }
                } catch (Exception e) {
                    System.err.println("Error in main menu loop: " + e.getMessage());
                    e.printStackTrace();
                    errorAnimation("An unexpected error occurred. Please try again.");
                    pause();
                }
            }
        } catch (Exception e) {
            System.err.println("Critical error in main application: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                try {
                    scanner.close();
                } catch (Exception e) {
                    System.err.println("Error closing scanner: " + e.getMessage());
                }
            }
        }
    }

    private static void showWelcomeScreen() {
        try {
            clearScreen();

            // Add vertical spacing to center content
            System.out.println("\n\n\n\n");

            // ASCII art for money/savings theme
            drawSavingsArt();

            // Title
            sleep(300);
            System.out.println(CYAN + BOLD + centerLine("PERSONAL FINANCE TRACKER", WIDTH) + RESET);
            sleep(200);
            System.out.println(YELLOW + centerLine("Take Control of Your Money", WIDTH) + RESET);
            System.out.println();

            String boxContent = "Track | Budget | Save | Achieve Your Goals";
            int boxWidth = boxContent.length() + 4;
            int leftPadding = (WIDTH - boxWidth) / 2;

            System.out.println(" ".repeat(leftPadding) + GREEN + "╔" + "═".repeat(boxWidth) + "╗" + RESET);
            System.out.println(" ".repeat(leftPadding) + GREEN + "║  " + boxContent + "  ║" + RESET);
            System.out.println(" ".repeat(leftPadding) + GREEN + "╚" + "═".repeat(boxWidth) + "╝" + RESET);
            System.out.println("\n");

            System.out.println(
                    BOLD + CYAN + centerLine("Start your journey to financial freedom - Press ENTER", WIDTH) + RESET);
            scanner.nextLine();
        } catch (Exception e) {
            System.err.println("Error displaying welcome screen: " + e.getMessage());
        }
    }

    private static void drawSavingsArt() {
        try {
            String[] art = {
                    "╔═══════════════════════════════════╗",
                    "║           S A V I N G S           ║",
                    "╠═══════════════════════════════════╣",
                    "║          ┌──────────────┐         ║",
                    "║          │  $  $  $  $  │         ║",
                    "║          │  $  $  $  $  │         ║",
                    "║          │  $  $  $  $  │         ║",
                    "║          └──────────────┘         ║",
                    "╠═══════════════════════════════════╣",
                    "║   Building Your Financial Future  ║",
                    "╚═══════════════════════════════════╝"
            };

            for (String line : art) {
                System.out.println(GREEN + centerLine(line, WIDTH) + RESET);
                sleep(60);
            }
            System.out.println();
        } catch (Exception e) {
            System.err.println("Error drawing savings art: " + e.getMessage());
        }
    }

    private static void showAuthMenu() {
        try {
            clearScreen();
            printSimpleHeader("USER AUTHENTICATION");

            printBoxTop(50);
            printCentered("1. Register New Account", 50, YELLOW);
            printCentered("2. Login to Your Account", 50, YELLOW);
            printCentered("3. Exit Application", 50, YELLOW);
            printBoxBottom(50);
            System.out.print(CYAN + "Enter choice: " + RESET);

            int choice = getIntInput();
            switch (choice) {
                case 1 -> registerUser();
                case 2 -> loginUser();
                case 3 -> showExitScreen();
                default -> {
                    shakeText("[ERROR] Invalid choice. Please try again.", RED);
                    pause();
                }
            }
        } catch (Exception e) {
            System.err.println("Error in authentication menu: " + e.getMessage());
            errorAnimation("An error occurred. Please try again.");
            pause();
        }
    }

    private static void showMainMenu() {
        try {
            clearScreen();
            printSimpleHeader("MAIN DASHBOARD");

            System.out.println();
            System.out.println(
                    GREEN + BOLD + "    Welcome back, " + userManager.getCurrentUser().getUsername() + "!" + RESET);
            System.out.println();

            // Show notifications first
            showNotificationsBanner();

            showQuickStats();
            System.out.println();

            printMenuSection("TRANSACTIONS & BUDGETS", new String[] {
                    "1. Add New Transaction",
                    "2. View All Transactions",
                    "3. Set Budget Limit",
                    "4. View Budget Status",
                    "5. Delete Transaction"
            });

            printMenuSection("FINANCIAL GOALS", new String[] {
                    "6. Create New Financial Goal",
                    "7. Update Goal Progress",
                    "8. View All Goals"
            });

            printMenuSection("ANALYTICS & REPORTS", new String[] {
                    "9. Financial Analytics Dashboard",
                    "10. Monthly Spending Report",
                    "11. Income vs Expense Analysis",
                    "12. Generate Comprehensive Report",
                    "13. View All Notifications"
            });

            printMenuSection("SETTINGS & ACCOUNT", new String[] {
                    "14. Update Monthly Income",
                    "15. View Profile",
                    "16. Logout"
            });

            System.out.print(CYAN + "Enter choice: " + RESET);

            int choice = getIntInput();
            fadeOut();

            switch (choice) {
                case 1 -> addTransaction();
                case 2 -> viewTransactions();
                case 3 -> setBudget();
                case 4 -> viewBudgets();
                case 5 -> deleteTransaction();
                case 6 -> createFinancialGoal();
                case 7 -> updateGoalProgress();
                case 8 -> viewFinancialGoals();
                case 9 -> viewAnalytics();
                case 10 -> viewMonthlyReport();
                case 11 -> viewIncomeExpenseAnalysis();
                case 12 -> generateComprehensiveReport();
                case 13 -> viewAllNotifications();
                case 14 -> updateMonthlyIncome();
                case 15 -> viewProfile();
                case 16 -> {
                    userManager.logout();
                    successAnimation("Logged out successfully!");
                    pause();
                }
                default -> {
                    shakeText("[ERROR] Invalid choice. Please try again.", RED);
                    pause();
                }
            }
        } catch (Exception e) {
            System.err.println("Error in main menu: " + e.getMessage());
            e.printStackTrace();
            errorAnimation("An error occurred. Please try again.");
            pause();
        }
    }

    private static void showNotificationsBanner() {
        try {
            if (financeManager == null)
                return;

            financeManager.checkForNotifications();
            List<String> notifications = financeManager.getNotifications();

            if (!notifications.isEmpty()) {
                System.out.println();
                printBoxTop(80);

                // Check for critical notifications (negative balance)
                boolean hasCritical = notifications.stream()
                        .anyMatch(n -> n.contains("NEGATIVE") || n.contains("CRITICAL"));

                if (hasCritical) {
                    printCentered("  CRITICAL ALERTS  ", 80, RED + BOLD);
                } else {
                    printCentered("NOTIFICATIONS (" + notifications.size() + ")", 80, YELLOW + BOLD);
                }

                printBoxDivider(80);

                // Show up to 3 most important notifications
                for (int i = 0; i < Math.min(3, notifications.size()); i++) {
                    String notif = notifications.get(i);
                    String color = notif.contains("CRITICAL") || notif.contains("NEGATIVE") ? RED + BOLD
                            : notif.contains("WARNING") || notif.contains("ALERT") ? YELLOW : CYAN;

                    // Remove color codes for length calculation
                    String cleanNotif = notif.replaceAll("\u001B\\[[;\\d]*m", "");
                    String displayText = cleanNotif;
                    if (displayText.length() > 76) {
                        displayText = displayText.substring(0, 73) + "...";
                    }

                    int padding = 76 - displayText.length();
                    System.out.println(CYAN + "║ " + RESET + color + displayText +
                            " ".repeat(Math.max(0, padding)) + CYAN + " ║" + RESET);
                    sleep(50);
                }

                if (notifications.size() > 3) {
                    String moreText = "... and " + (notifications.size() - 3) + " more notification(s)";
                    int padding = 76 - moreText.length();
                    System.out.println(CYAN + "║ " + RESET + DIM + moreText +
                            " ".repeat(Math.max(0, padding)) + CYAN + " ║" + RESET);
                }

                printBoxBottom(80);
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("Error showing notifications banner: " + e.getMessage());
        }
    }

    private static void viewAllNotifications() {
        try {
            clearScreen();
            printSimpleHeader("NOTIFICATION CENTER");

            System.out.println();

            financeManager.checkForNotifications();
            List<String> notifications = financeManager.getNotifications();

            if (notifications.isEmpty()) {
                System.out.println(GREEN + "✓ No notifications - All clear!" + RESET);
                System.out.println(CYAN + "\nYour finances are looking good!" + RESET);
            } else {
                printBoxTop(90);
                String headerText = "Total Notifications: " + notifications.size();
                printCentered(headerText, 90, YELLOW + BOLD);
                printBoxDivider(90);

                for (int i = 0; i < notifications.size(); i++) {
                    String notif = notifications.get(i);
                    String icon = notif.contains("CRITICAL") || notif.contains("NEGATIVE") ? "[!!!]"
                            : notif.contains("WARNING") || notif.contains("ALERT") ? "[!]"
                                    : notif.contains("Congratulations") ? "[✓]" : "[i]";
                    String color = notif.contains("CRITICAL") || notif.contains("NEGATIVE") ? RED + BOLD
                            : notif.contains("WARNING") || notif.contains("ALERT") ? YELLOW
                                    : notif.contains("Congratulations") ? GREEN : CYAN;

                    // Remove color codes for display
                    String cleanNotif = notif.replaceAll("\u001B\\[[;\\d]*m", "");
                    String displayLine = (i + 1) + ". " + icon + " " + cleanNotif;

                    if (displayLine.length() > 86) {
                        displayLine = displayLine.substring(0, 83) + "...";
                    }

                    int padding = 86 - displayLine.length();
                    System.out.println(CYAN + "║ " + RESET + color + displayLine +
                            " ".repeat(Math.max(0, padding)) + CYAN + " ║" + RESET);
                    sleep(60);
                }

                printBoxBottom(90);

                System.out.println();
                System.out.print(YELLOW + "Clear all notifications? (y/n): " + RESET);
                String choice = scanner.nextLine();

                if (choice.equalsIgnoreCase("y")) {
                    financeManager.clearNotifications();
                    successAnimation("All notifications cleared!");
                }
            }
            pause();
        } catch (Exception e) {
            System.err.println("Error viewing notifications: " + e.getMessage());
            errorAnimation("Error loading notifications");
            pause();
        }
    }

    private static void generateComprehensiveReport() {
        try {
            clearScreen();
            printSimpleHeader("GENERATE COMPREHENSIVE REPORT");

            System.out.println();
            System.out.println(CYAN + "Generating detailed financial report..." + RESET);

            // Animated progress bar
            for (int i = 0; i <= 100; i += 10) {
                System.out.print("\r" + YELLOW + "[" + "=".repeat(i / 10) +
                        " ".repeat(10 - i / 10) + "] " + i + "%" + RESET);
                sleep(150);
            }
            System.out.println();
            System.out.println();

            financeManager.saveComprehensiveReport();
            String reportContent = financeManager.generateComprehensiveReport();

            successAnimation("Report generated successfully!");
            System.out.println();

            printBoxTop(100);
            String reportPath = "Report saved to: data/reports/" + userManager.getCurrentUser().getUsername()
                    + "_report_*.txt";
            printCentered(reportPath, 100, GREEN + BOLD);
            printBoxBottom(100);

            System.out.println();
            System.out.print(YELLOW + "Would you like to preview the report? (y/n): " + RESET);
            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("y")) {
                clearScreen();
                printSimpleHeader("FINANCIAL REPORT PREVIEW");
                System.out.println();
                System.out.println(reportContent);
            }

            pause();
        } catch (Exception e) {
            System.err.println("Error generating report: " + e.getMessage());
            e.printStackTrace();
            errorAnimation("Error generating comprehensive report");
            pause();
        }
    }

    private static void registerUser() {
        try {
            clearScreen();
            printSimpleHeader("CREATE NEW ACCOUNT");

            System.out.println();
            System.out.print(CYAN + "Enter username: " + RESET);
            String username = scanner.nextLine();
            System.out.print(CYAN + "Enter password: " + RESET);
            String password = scanner.nextLine();
            System.out.print(CYAN + "Enter email: " + RESET);
            String email = scanner.nextLine();
            System.out.print(CYAN + "Enter monthly income: $" + RESET);
            double income = getDoubleInput();

            System.out.println();

            if (userManager.registerUser(username, password, email, income)) {
                successAnimation("Registration successful! Welcome aboard!");
                System.out.println(YELLOW + "\nPlease login to access your account." + RESET);
            } else {
                errorAnimation("Registration failed!");
                System.out.println(YELLOW + "Username may already exist or email is invalid." + RESET);
            }
            pause();
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            errorAnimation("Registration error occurred");
            pause();
        }
    }

    private static void loginUser() {
        try {
            clearScreen();
            printSimpleHeader("LOGIN TO YOUR ACCOUNT");

            System.out.println();
            System.out.print(CYAN + "Enter username: " + RESET);
            String username = scanner.nextLine();
            System.out.print(CYAN + "Enter password: " + RESET);
            String password = scanner.nextLine();

            System.out.println();

            if (userManager.loginUser(username, password)) {
                financeManager = new FinanceManager(userManager.getCurrentUser());
                successAnimation("Login successful! Welcome back, " + username + "!");
                sleep(900);
            } else {
                errorAnimation("Login failed! Invalid credentials.");
                pause();
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            errorAnimation("Login error occurred");
            pause();
        }
    }

    private static void addTransaction() {
        try {
            clearScreen();
            printSimpleHeader("ADD NEW TRANSACTION");

            System.out.println();
            System.out.println(CYAN + " Current Balance: " +
                    String.format("$%.2f", financeManager.getCurrentBalance()) + RESET);
            System.out.println();

            System.out.print(CYAN + "Enter date (YYYY-MM-DD) or press ENTER for today: " + RESET);
            String dateInput = scanner.nextLine();
            LocalDate date;
            
            try {
                date = dateInput.isEmpty() ? LocalDate.now() : LocalDate.parse(dateInput);
            } catch (DateTimeParseException e) {
                System.err.println("Invalid date format: " + e.getMessage());
                errorAnimation("Invalid date format! Please use YYYY-MM-DD");
                pause();
                return;
            }

            System.out.print(CYAN + "Enter description: " + RESET);
            String description = scanner.nextLine();

            System.out.print(CYAN + "Enter category: " + RESET);
            String category = scanner.nextLine();

            System.out.print(CYAN + "Enter amount: $" + RESET);
            double amount = getDoubleInput();

            System.out.println(YELLOW + "\nSelect transaction type:" + RESET);
            System.out.println("  1. [+] Income");
            System.out.println("  2. [-] Expense");
            System.out.print(YELLOW + "Your choice: " + RESET);
            int typeChoice = getIntInput();
            Transaction.TransactionType type = (typeChoice == 1) ? Transaction.TransactionType.INCOME
                    : Transaction.TransactionType.EXPENSE;

            System.out.println();

            if (financeManager.addTransaction(date, description, category, amount, type)) {
                successAnimation("Transaction added successfully!");

                double newBalance = financeManager.getCurrentBalance();
                System.out.println(CYAN + "\nNew Balance: " + RESET +
                        (newBalance < 0 ? RED + BOLD : GREEN) +
                        String.format("$%.2f", newBalance) + RESET);

                if (newBalance < 0) {
                    System.out.println();
                    shakeText("  WARNING: Your balance is now NEGATIVE!", RED);
                }

                if (type == Transaction.TransactionType.EXPENSE) {
                    System.out.println(YELLOW + "Budget tracking updated automatically." + RESET);
                }
            } else {
                errorAnimation("Failed to add transaction!");
            }
            pause();
        } catch (Exception e) {
            System.err.println("Error adding transaction: " + e.getMessage());
            e.printStackTrace();
            errorAnimation("Error occurred while adding transaction");
            pause();
        }
    }

    private static void viewTransactions() {
        try {
            clearScreen();
            printSimpleHeader("VIEW TRANSACTIONS");

            printBoxTop(50);
            printCentered("1. View All Transactions", 50, YELLOW);
            printCentered("2. View by Category", 50, YELLOW);
            printCentered("3. View by Date Range", 50, YELLOW);
            printBoxBottom(50);
            System.out.print(CYAN + "Enter choice: " + RESET);

            int choice = getIntInput();
            List<Transaction> transactions;

            System.out.println();

            switch (choice) {
                case 1 -> transactions = financeManager.getTransactions();
                case 2 -> {
                    System.out.print(CYAN + "Enter category: " + RESET);
                    String category = scanner.nextLine();
                    transactions = financeManager.getTransactionsByCategory(category);
                }
                case 3 -> {
                    try {
                        System.out.print(CYAN + "Enter start date (YYYY-MM-DD): " + RESET);
                        LocalDate start = LocalDate.parse(scanner.nextLine());
                        System.out.print(CYAN + "Enter end date (YYYY-MM-DD): " + RESET);
                        LocalDate end = LocalDate.parse(scanner.nextLine());
                        transactions = financeManager.getTransactionsByDateRange(start, end);
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid date format: " + e.getMessage());
                        errorAnimation("Invalid date format!");
                        pause();
                        return;
                    }
                }
                default -> {
                    errorAnimation("Invalid choice!");
                    pause();
                    return;
                }
            }

            System.out.println();
            clearScreen();
            printSimpleHeader("TRANSACTION RESULTS");

            if (transactions.isEmpty()) {
                System.out.println(YELLOW + "\nNo transactions found." + RESET);
            } else {
                System.out.println(GREEN + "\nFound " + transactions.size() + " transaction(s)\n" + RESET);
                printBoxTop(100);
                for (int i = 0; i < transactions.size(); i++) {
                    Transaction t = transactions.get(i);
                    String icon = t.getType() == Transaction.TransactionType.INCOME ? "[+]" : "[-]";
                    System.out.println(CYAN + "║ " + RESET + icon + " " + t.toString());
                    sleep(40);
                }
                printBoxBottom(100);
            }
            pause();
        } catch (Exception e) {
            System.err.println("Error viewing transactions: " + e.getMessage());
            errorAnimation("Error loading transactions");
            pause();
        }
    }

    private static void deleteTransaction() {
        try {
            clearScreen();
            printSimpleHeader("DELETE TRANSACTION");

            List<Transaction> transactions = financeManager.getTransactions();
            if (transactions.isEmpty()) {
                System.out.println(YELLOW + "\nNo transactions to delete." + RESET);
                pause();
                return;
            }

            System.out.println(CYAN + "\nYour Recent Transactions:\n" + RESET);
            for (int i = 0; i < Math.min(10, transactions.size()); i++) {
                System.out.println((i + 1) + ". " + transactions.get(i));
                sleep(40);
            }

            System.out.print(YELLOW + "\nEnter transaction ID (first 8 characters): " + RESET);
            String transactionId = scanner.nextLine();

            System.out.println();

            if (financeManager.deleteTransaction(transactionId)) {
                successAnimation("Transaction deleted successfully!");
            } else {
                errorAnimation("Transaction not found!");
            }
            pause();
        } catch (Exception e) {
            System.err.println("Error deleting transaction: " + e.getMessage());
            errorAnimation("Error occurred during deletion");
            pause();
        }
    }

    private static void setBudget() {
        try {
            clearScreen();
            printSimpleHeader("SET BUDGET LIMIT");

            System.out.println();
            System.out.print(CYAN + "Enter category: " + RESET);
            String category = scanner.nextLine();
            System.out.print(CYAN + "Enter budget amount: $" + RESET);
            double amount = getDoubleInput();

            YearMonth month = YearMonth.now();
            System.out.print(CYAN + "Enter month (YYYY-MM) or press ENTER for current: " + RESET);
            String monthInput = scanner.nextLine();
            
            if (!monthInput.isEmpty()) {
                try {
                    month = YearMonth.parse(monthInput);
                } catch (DateTimeParseException e) {
                    System.err.println("Invalid month format: " + e.getMessage());
                    errorAnimation("Invalid month format! Please use YYYY-MM");
                    pause();
                    return;
                }
            }

            System.out.println();

            if (financeManager.setBudget(category, amount, month)) {
                successAnimation("Budget set successfully!");
                System.out.println(YELLOW + "We'll track your spending automatically." + RESET);
            } else {
                errorAnimation("Failed to set budget!");
            }
            pause();
        } catch (Exception e) {
            System.err.println("Error setting budget: " + e.getMessage());
            errorAnimation("Error occurred while setting budget");
            pause();
        }
    }

    private static void viewBudgets() {
        try {
            clearScreen();
            printSimpleHeader("BUDGET STATUS OVERVIEW");

            System.out.println();
            printBoxTop(90);
            printCentered("Current Month: " + YearMonth.now(), 90, GREEN);
            printBoxDivider(90);

            List<Budget> budgets = financeManager.getCurrentMonthBudgets();

            if (budgets.isEmpty()) {
                printCentered("No budgets set for current month.", 90, YELLOW);
                printCentered("Set budgets to track your spending limits!", 90, CYAN);
            } else {
                for (Budget b : budgets) {
                    sleep(90);
                    double usage = b.getUsagePercentage();
                    String bar = getProgressBar(usage, 30);
                    String status = b.isExceeded() ? "[X] EXCEEDED" : usage > 80 ? "[!] WARNING" : "[OK] GOOD";
                    String color = b.isExceeded() ? RED : usage > 80 ? YELLOW : GREEN;

                    String categoryLine = color + status + RESET + " " + b.getCategory() + ": " + bar + " "
                            + String.format("%.1f%%", usage);
                    String amountLine = "Spent: $%.2f / $%.2f  |  Remaining: $%.2f".formatted(b.getSpent(), b.getAmount(),
                            b.getRemaining());

                    // Remove color codes for length calculation
                    String cleanCategoryLine = categoryLine.replaceAll("\u001B\\[[;\\d]*m", "");
                    String cleanAmountLine = amountLine.replaceAll("\u001B\\[[;\\d]*m", "");

                    System.out.println(CYAN + "║ " + RESET + categoryLine
                            + " ".repeat(Math.max(0, 88 - cleanCategoryLine.length())) + CYAN + "║" + RESET);
                    System.out.println(CYAN + "║   " + RESET + amountLine
                            + " ".repeat(Math.max(0, 86 - cleanAmountLine.length())) + CYAN + "║" + RESET);
                }

                List<Budget> exceeded = financeManager.getExceededBudgets();
                if (!exceeded.isEmpty()) {
                    printBoxDivider(90);
                    printCentered("BUDGET ALERTS - OVER LIMIT", 90, RED + BOLD);
                    printBoxDivider(90);
                    exceeded.forEach(b -> {
                        String alertText = b.getCategory() + " exceeded by $"
                                + String.format("%.2f", Math.abs(b.getRemaining()));
                        printCentered(alertText, 90, RED);
                        sleep(90);
                    });
                }
            }
            printBoxBottom(90);
            pause();
        } catch (Exception e) {
            System.err.println("Error viewing budgets: " + e.getMessage());
            errorAnimation("Error loading budget information");
            pause();
        }
    }

    private static void createFinancialGoal() {
        try {
            clearScreen();
            printSimpleHeader("CREATE FINANCIAL GOAL");

            System.out.println();
            System.out.print(CYAN + "Enter goal name: " + RESET);
            String name = scanner.nextLine();
            System.out.print(CYAN + "Enter description: " + RESET);
            String description = scanner.nextLine();
            System.out.print(CYAN + "Enter target amount: $" + RESET);
            double targetAmount = getDoubleInput();
            System.out.print(CYAN + "Enter target date (YYYY-MM-DD): " + RESET);
            
            LocalDate targetDate;
            try {
                targetDate = LocalDate.parse(scanner.nextLine());
            } catch (DateTimeParseException e) {
                System.err.println("Invalid date format: " + e.getMessage());
                errorAnimation("Invalid date format! Please use YYYY-MM-DD");
                pause();
                return;
            }

            System.out.println();

            if (financeManager.createFinancialGoal(name, description, targetAmount, targetDate)) {
                successAnimation("Financial goal created!");
                System.out.println(YELLOW + "Start tracking your progress towards your goal!" + RESET);
            } else {
                errorAnimation("Failed to create goal!");
            }
            pause();
        } catch (Exception e) {
            System.err.println("Error creating financial goal: " + e.getMessage());
            errorAnimation("Error occurred while creating goal");
            pause();
        }
    }

    private static void updateGoalProgress() {
        try {
            clearScreen();
            printSimpleHeader("UPDATE GOAL PROGRESS");

            List<FinancialGoal> goals = financeManager.getFinancialGoals();
            if (goals.isEmpty()) {
                System.out.println(YELLOW + "\nNo financial goals found." + RESET);
                System.out.println(CYAN + "Create a goal first to start tracking!" + RESET);
                pause();
                return;
            }

            System.out.println(CYAN + "\nYour Goals:\n" + RESET);
            for (int i = 0; i < goals.size(); i++) {
                FinancialGoal g = goals.get(i);
                System.out.println((i + 1) + ". " + g.getName() +
                        " - Progress: $" + g.getCurrentAmount() +
                        "/$" + g.getTargetAmount());
                sleep(50);
            }

            System.out.print(YELLOW + "\nEnter goal ID (first 8 characters): " + RESET);
            String goalId = scanner.nextLine();
            System.out.print(CYAN + "Enter amount to add: $" + RESET);
            double amount = getDoubleInput();

            System.out.println();

            if (financeManager.updateGoalProgress(goalId, amount)) {
                successAnimation("Goal progress updated!");
                System.out.println(YELLOW + "Keep going, you're making great progress!" + RESET);
            } else {
                errorAnimation("Goal not found!");
            }
            pause();
        } catch (Exception e) {
            System.err.println("Error updating goal progress: " + e.getMessage());
            errorAnimation("Error occurred while updating goal");
            pause();
        }
    }

    private static void viewFinancialGoals() {
        try {
            clearScreen();
            printSimpleHeader("FINANCIAL GOALS TRACKER");

            System.out.println();
            List<FinancialGoal> goals = financeManager.getFinancialGoals();

            if (goals.isEmpty()) {
                System.out.println(YELLOW + "No financial goals set yet." + RESET);
                System.out.println(CYAN + "Set financial goals to stay motivated!" + RESET);
            } else {
                printBoxTop(100);
                printCentered("Total Goals: " + goals.size(), 100, GREEN);
                printBoxDivider(100);

                for (FinancialGoal goal : goals) {
                    sleep(120);
                    double progress = goal.getProgressPercentage();
                    String bar = getProgressBar(progress, 40);
                    String status = goal.isCompleted() ? "[OK] COMPLETED"
                            : goal.isOverdue() ? "[X] OVERDUE" : "[~] IN PROGRESS";
                    String color = goal.isCompleted() ? GREEN : goal.isOverdue() ? RED : YELLOW;

                    String nameLine = color + BOLD + status + RESET + " " + goal.getName();
                    String progressLine = bar + " " + String.format("%.1f%%", progress);
                    String amountLine = "$%.2f / $%.2f  |  %d days remaining".formatted(
                            goal.getCurrentAmount(), goal.getTargetAmount(), goal.getDaysRemaining());

                    // Remove color codes for length calculation
                    String cleanNameLine = nameLine.replaceAll("\u001B\\[[;\\d]*m", "");
                    String cleanProgressLine = progressLine.replaceAll("\u001B\\[[;\\d]*m", "");
                    String cleanAmountLine = amountLine.replaceAll("\u001B\\[[;\\d]*m", "");

                    System.out.println(CYAN + "║ " + RESET + nameLine + " ".repeat(Math.max(0, 98 - cleanNameLine.length()))
                            + CYAN + "║" + RESET);
                    System.out.println(CYAN + "║   " + RESET + progressLine
                            + " ".repeat(Math.max(0, 96 - cleanProgressLine.length())) + CYAN + "║" + RESET);
                    System.out.println(CYAN + "║   " + RESET + amountLine
                            + " ".repeat(Math.max(0, 96 - cleanAmountLine.length())) + CYAN + "║" + RESET);
                }

                long completed = goals.stream().filter(FinancialGoal::isCompleted).count();
                long overdue = goals.stream().filter(FinancialGoal::isOverdue).count();
                long inProgress = goals.size() - completed - overdue;

                printBoxDivider(100);
                String statsLine = "Completed: " + completed + " | In Progress: " + inProgress + " | Overdue: " + overdue;
                printCentered(statsLine, 100, CYAN);
                printBoxBottom(100);
            }
            pause();
        } catch (Exception e) {
            System.err.println("Error viewing financial goals: " + e.getMessage());
            errorAnimation("Error loading goals");
            pause();
        }
    }

    private static void viewAnalytics() {
        try {
            clearScreen();
            printSimpleHeader("FINANCIAL ANALYTICS DASHBOARD");

            System.out.println();

            double balance = financeManager.getCurrentBalance();
            double savingsRate = financeManager.getMonthlySavingsRate();
            Map<String, Double> spending = financeManager.getCategoryWiseSpending();
            List<Budget> exceededBudgets = financeManager.getExceededBudgets();

            System.out.println("\n");
            printBoxTop(80);

            // Negative balance alert
            if (balance < 0) {
                printCentered("⚠ CRITICAL ALERT ⚠", 80, RED + BOLD);
                printBoxDivider(80);
                String balanceStr = String.format("$%.2f", Math.abs(balance));
                printCentered("YOUR BALANCE IS NEGATIVE!", 80, RED + BOLD);
                printCentered("Amount: -" + balanceStr, 80, RED + BOLD);
                printCentered("Please add income or reduce expenses urgently!", 80, YELLOW);
                printBoxDivider(80);
            }

            String balanceStr = String.format("$%.2f", Math.abs(balance));
            String balanceColor = balance >= 0 ? GREEN : RED;
            String balanceIcon = balance >= 0 ? "[+]" : "[!]";
            printCentered(balanceIcon + " CURRENT BALANCE: " + (balance < 0 ? "-" : "") + balanceStr, 80,
                    balanceColor + BOLD);

            String savingsStatus = savingsRate >= 20 ? "Excellent!"
                    : savingsRate >= 10 ? "Good" : savingsRate >= 0 ? "Needs Improvement" : "Warning - Overspending!";
            String savingsColor = savingsRate >= 20 ? GREEN : savingsRate >= 10 ? CYAN : savingsRate >= 0 ? YELLOW : RED;
            printCentered("SAVINGS RATE: " + String.format("%.1f%%", savingsRate) + " - " + savingsStatus, 80,
                    savingsColor);

            printBoxDivider(80);
            printCentered("SPENDING BY CATEGORY", 80, PURPLE + BOLD);
            printBoxDivider(80);

            if (spending.isEmpty()) {
                printCentered("No spending data available", 80, YELLOW);
            } else {
                spending.forEach((category, amount) -> {
                    String categoryLine = category + ": $" + String.format("%.2f", amount);
                    printCentered(categoryLine, 80, YELLOW);
                    sleep(90);
                });
            }

            if (!exceededBudgets.isEmpty()) {
                printBoxDivider(80);
                printCentered("BUDGET ALERTS", 80, RED + BOLD);
                printBoxDivider(80);
                exceededBudgets.forEach(b -> {
                    String alertText = "[X] " + b.getCategory() + " exceeded by $"
                            + String.format("%.2f", Math.abs(b.getRemaining()));
                    printCentered(alertText, 80, RED);
                    sleep(90);
                });
            }

            printBoxBottom(80);
            pause();
        } catch (Exception e) {
            System.err.println("Error viewing analytics: " + e.getMessage());
            errorAnimation("Error loading analytics");
            pause();
        }
    }

    private static void viewMonthlyReport() {
        try {
            clearScreen();
            printSimpleHeader("MONTHLY SPENDING REPORT");

            System.out.println();

            YearMonth currentMonth = YearMonth.now();
            LocalDate firstDay = currentMonth.atDay(1);
            LocalDate lastDay = currentMonth.atEndOfMonth();

            List<Transaction> monthlyTransactions = financeManager.getTransactionsByDateRange(firstDay, lastDay);
            double totalIncome = monthlyTransactions.stream()
                    .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            double totalExpenses = monthlyTransactions.stream()
                    .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            double netSavings = totalIncome - totalExpenses;

            System.out.println("\n");
            printBoxTop(80);
            printCentered("Month: " + currentMonth, 80, CYAN + BOLD);
            printBoxDivider(80);
            printCentered("Total Income: $" + String.format("%.2f", totalIncome), 80, GREEN);
            sleep(180);
            printCentered("Total Expenses: $" + String.format("%.2f", totalExpenses), 80, RED);
            sleep(180);
            printCentered("Net Savings: $" + String.format("%.2f", netSavings), 80, PURPLE + BOLD);
            sleep(180);

            if (totalIncome > 0) {
                double expenseRatio = (totalExpenses / totalIncome) * 100;
                printCentered("Expense to Income Ratio: " + String.format("%.1f%%", expenseRatio), 80, YELLOW);
            }

            printBoxBottom(80);
            pause();
        } catch (Exception e) {
            System.err.println("Error viewing monthly report: " + e.getMessage());
            errorAnimation("Error generating monthly report");
            pause();
        }
    }

    private static void viewIncomeExpenseAnalysis() {
        try {
            clearScreen();
            printSimpleHeader("INCOME VS EXPENSE ANALYSIS");

            System.out.println();

            YearMonth currentMonth = YearMonth.now();
            LocalDate firstDay = currentMonth.atDay(1);
            LocalDate lastDay = currentMonth.atEndOfMonth();

            List<Transaction> monthlyTransactions = financeManager.getTransactionsByDateRange(firstDay, lastDay);
            double totalIncome = monthlyTransactions.stream()
                    .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            double totalExpenses = monthlyTransactions.stream()
                    .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            System.out.println("\n");
            printBoxTop(80);
            printCentered("Month: " + currentMonth, 80, CYAN + BOLD);
            printBoxDivider(80);

            double max = Math.max(totalIncome, totalExpenses);
            int scale = 50;

            String incomeBar = getAnimatedBar(totalIncome, max, scale, GREEN);
            String expenseBar = getAnimatedBar(totalExpenses, max, scale, RED);

            String incomeLine = "INCOME:   " + incomeBar + " $" + String.format("%.2f", totalIncome);
            String expenseLine = "EXPENSES: " + expenseBar + " $" + String.format("%.2f", totalExpenses);

            // Remove color codes for length calculation
            String cleanIncomeLine = incomeLine.replaceAll("\u001B\\[[;\\d]*m", "");
            String cleanExpenseLine = expenseLine.replaceAll("\u001B\\[[;\\d]*m", "");

            System.out.println(CYAN + "║ " + RESET + incomeLine + " ".repeat(Math.max(0, 78 - cleanIncomeLine.length()))
                    + CYAN + "║" + RESET);
            System.out.println(CYAN + "║ " + RESET + expenseLine + " ".repeat(Math.max(0, 78 - cleanExpenseLine.length()))
                    + CYAN + "║" + RESET);

            printBoxDivider(80);
            double difference = totalIncome - totalExpenses;
            if (difference > 0) {
                printCentered("You saved $" + String.format("%.2f", difference) + " this month!", 80, GREEN + BOLD);
            } else if (difference < 0) {
                printCentered("You overspent by $" + String.format("%.2f", Math.abs(difference)), 80, RED + BOLD);
            } else {
                printCentered("Income and expenses are balanced.", 80, YELLOW);
            }

            printBoxBottom(80);
            pause();
        } catch (Exception e) {
            System.err.println("Error viewing income/expense analysis: " + e.getMessage());
            errorAnimation("Error generating analysis");
            pause();
        }
    }

    private static void updateMonthlyIncome() {
        try {
            clearScreen();
            printSimpleHeader("UPDATE MONTHLY INCOME");

            User currentUser = userManager.getCurrentUser();
            System.out.println();
            System.out.println(CYAN + "Current monthly income: $" +
                    String.format("%.2f", currentUser.getMonthlyIncome()) + RESET);
            System.out.print(CYAN + "Enter new monthly income: $" + RESET);
            double newIncome = getDoubleInput();

            System.out.println();

            if (userManager.updateUserIncome(newIncome)) {
                successAnimation("Income updated successfully!");
                System.out.println(YELLOW + "Your calculations will be updated accordingly." + RESET);
            } else {
                errorAnimation("Failed to update income!");
            }
            pause();
        } catch (Exception e) {
            System.err.println("Error updating monthly income: " + e.getMessage());
            errorAnimation("Error occurred during update");
            pause();
        }
    }

    private static void viewProfile() {
        try {
            clearScreen();
            printSimpleHeader("USER PROFILE");

            System.out.println();

            User currentUser = userManager.getCurrentUser();
            double balance = financeManager.getCurrentBalance();

            System.out.println("\n");
            printBoxTop(80);
            printCentered(currentUser.getUsername(), 80, CYAN + BOLD);
            printBoxDivider(80);
            sleep(100);
            printCentered(currentUser.getEmail(), 80, CYAN);
            sleep(100);
            printCentered("Monthly Income: $" + String.format("%.2f", currentUser.getMonthlyIncome()), 80, GREEN);
            sleep(100);

            String balanceStr = String.format("$%.2f", Math.abs(balance));
            String balanceColor = balance >= 0 ? GREEN : RED;
            printCentered("Balance: " + (balance < 0 ? "-" : "") + balanceStr, 80, balanceColor);
            sleep(100);
            printCentered("Member Since: " + currentUser.getCreatedAt().format(DateTimeFormatter.ISO_DATE), 80, CYAN);

            printBoxDivider(80);
            printCentered("QUICK STATS", 80, YELLOW + BOLD);
            printBoxDivider(80);
            sleep(100);
            printCentered("Total Transactions: " + financeManager.getTransactions().size(), 80, RESET);
            sleep(100);
            printCentered("Active Budgets: " + financeManager.getBudgets().size(), 80, RESET);
            sleep(100);
            printCentered("Financial Goals: " + financeManager.getFinancialGoals().size(), 80, RESET);
            printBoxBottom(80);

            pause();
        } catch (Exception e) {
            System.err.println("Error viewing profile: " + e.getMessage());
            errorAnimation("Error loading profile");
            pause();
        }
    }

    private static void showExitScreen() {
        try {
            clearScreen();
            System.out.println();

            String[] goodbye = {
                    "    ╔══════════════════════════════════════════════════════════╗",
                    "    ║                                                          ║",
                    "    ║                  THANK YOU FOR USING                     ║",
                    "    ║                PERSONAL FINANCE TRACKER                  ║",
                    "    ║                                                          ║",
                    "    ║                  Track | Budget | Save                   ║",
                    "    ║                                                          ║",
                    "    ║                   See You Next Time!                     ║",
                    "    ║                                                          ║",
                    "    ╚══════════════════════════════════════════════════════════╝"
            };

            for (String line : goodbye) {
                System.out.println(CYAN + line + RESET);
                sleep(90);
            }

            System.out.println();
            typewriterEffect(centerLine("Achieve Your Financial Goals!", WIDTH), GREEN, 32);
            System.out.println("\n");

            running = false;
        } catch (Exception e) {
            System.err.println("Error displaying exit screen: " + e.getMessage());
            running = false;
        }
    }

    private static void showQuickStats() {
        try {
            if (financeManager == null)
                return;

            double balance = financeManager.getCurrentBalance();
            int transactions = financeManager.getTransactions().size();
            int budgets = financeManager.getBudgets().size();
            int goals = financeManager.getFinancialGoals().size();

            printBoxTop(80);

            String balanceIcon = balance >= 0 ? "[+]" : "[!]";
            String balanceColor = balance >= 0 ? GREEN : RED + BOLD;
            String balanceStr = String.format("$%.2f", Math.abs(balance));

            // Build the complete line without color codes for length calculation
            String balanceText;
            if (balance < 0) {
                balanceText = balanceIcon + " Balance: NEGATIVE -" + balanceStr;
            } else {
                balanceText = balanceIcon + " Balance: " + balanceStr;
            }

            String transText = "Transactions: " + transactions;
            String budgetText = "Budgets: " + budgets;
            String goalText = "Goals: " + goals;

            // Combine all parts for total length calculation (without color codes)
            String fullLine = balanceText + "  |  " + transText + "  |  " + budgetText + "  | " + goalText;

            // Calculate padding needed
            int paddingNeeded = 76 - fullLine.length();

            // Build the actual output with colors
            System.out.print(CYAN + "║ " + RESET);

            // Balance part
            if (balance < 0) {
                System.out.print(balanceIcon + " Balance: " + balanceColor + "NEGATIVE -" + balanceStr + RESET);
            } else {
                System.out.print(balanceIcon + " Balance: " + balanceColor + balanceStr + RESET);
            }

            System.out.print("  |  " + YELLOW + transText + RESET);
            System.out.print("  |  " + PURPLE + budgetText + RESET);
            System.out.print("  | " + BLUE + goalText + RESET);

            // Add proper spacing
            System.out.print(" ".repeat(Math.max(0, paddingNeeded)));
            System.out.println(CYAN + " ║" + RESET);

            printBoxBottom(80);
        } catch (Exception e) {
            System.err.println("Error showing quick stats: " + e.getMessage());
        }
    }

    // ==================== UTILITY METHODS ====================

    private static void printSimpleHeader(String title) {
        try {
            printBoxTop(WIDTH);
            printCentered(title, WIDTH, CYAN + BOLD);
            printBoxBottom(WIDTH);
            System.out.println();
        } catch (Exception e) {
            System.err.println("Error printing header: " + e.getMessage());
        }
    }

    private static void successAnimation(String message) {
        try {
            for (int i = 0; i < 3; i++) {
                System.out.print("\r" + GREEN + "[OK] " + message + RESET);
                sleep(160);
                System.out.print("\r" + GREEN + "[OK] " + message + " ." + RESET);
                sleep(160);
                System.out.print("\r" + GREEN + "[OK] " + message + " .." + RESET);
                sleep(160);
                System.out.print("\r" + GREEN + "[OK] " + message + " ..." + RESET);
                sleep(160);
            }
            System.out.println("\r" + GREEN + BOLD + "[OK] " + message + "     " + RESET);
        } catch (Exception e) {
            System.err.println("Error in success animation: " + e.getMessage());
        }
    }

    private static void errorAnimation(String message) {
        try {
            for (int i = 0; i < 3; i++) {
                System.out.print("\r" + RED + "[X] " + message + RESET);
                sleep(140);
                System.out.print("\r" + RED + BOLD + "[X] " + message + RESET);
                sleep(140);
            }
            System.out.println();
        } catch (Exception e) {
            System.err.println("Error in error animation: " + e.getMessage());
        }
    }

    private static void typewriterEffect(String text, String color, int delayMs) {
        try {
            for (char c : text.toCharArray()) {
                System.out.print(color + c + RESET);
                System.out.flush();
                sleep(delayMs);
            }
        } catch (Exception e) {
            System.err.println("Error in typewriter effect: " + e.getMessage());
        }
    }

    private static void shakeText(String text, String color) {
        try {
            for (int i = 0; i < 3; i++) {
                System.out.print("\r  " + color + text + RESET);
                sleep(80);
                System.out.print("\r" + color + text + RESET);
                sleep(80);
            }
            System.out.println();
        } catch (Exception e) {
            System.err.println("Error in shake text: " + e.getMessage());
        }
    }

    private static void fadeOut() {
        try {
            for (int i = 0; i < 3; i++) {
                System.out.print(".");
                sleep(80);
            }
            System.out.println();
        } catch (Exception e) {
            System.err.println("Error in fade out: " + e.getMessage());
        }
    }

    private static String getProgressBar(double percentage, int length) {
        try {
            int filled = (int) ((percentage / 100.0) * length);
            StringBuilder bar = new StringBuilder("[");
            for (int i = 0; i < length; i++) {
                if (i < filled) {
                    bar.append(GREEN).append("█").append(RESET);
                } else {
                    bar.append(DIM).append("░").append(RESET);
                }
            }
            bar.append("]");
            return bar.toString();
        } catch (Exception e) {
            System.err.println("Error creating progress bar: " + e.getMessage());
            return "[" + " ".repeat(length) + "]";
        }
    }

    private static String getAnimatedBar(double value, double max, int scale, String color) {
        try {
            if (max == 0)
                return "[" + " ".repeat(scale) + "]";
            int length = (int) ((value / max) * scale);
            StringBuilder bar = new StringBuilder("[");
            for (int i = 0; i < scale; i++) {
                if (i < length) {
                    bar.append(color).append("█").append(RESET);
                    if (i % 4 == 0)
                        sleep(10);
                } else {
                    bar.append(DIM).append("░").append(RESET);
                }
            }
            bar.append("]");
            return bar.toString();
        } catch (Exception e) {
            System.err.println("Error creating animated bar: " + e.getMessage());
            return "[" + " ".repeat(scale) + "]";
        }
    }

    private static void printMenuSection(String title, String[] items) {
        try {
            System.out.println();
            System.out.println(PURPLE + BOLD + "  > " + title + RESET);
            System.out.println(DIM + "  " + "─".repeat(60) + RESET);
            for (String item : items) {
                System.out.println("    " + YELLOW + item + RESET);
            }
        } catch (Exception e) {
            System.err.println("Error printing menu section: " + e.getMessage());
        }
    }

    private static void clearLine() {
        try {
            System.out.print("\r" + " ".repeat(WIDTH) + "\r");
            System.out.flush();
        } catch (Exception e) {
            System.err.println("Error clearing line: " + e.getMessage());
        }
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Sleep interrupted: " + e.getMessage());
        }
    }

    private static void printBoxTop(int width) {
        try {
            System.out.println(CYAN + "╔" + "═".repeat(width) + "╗" + RESET);
        } catch (Exception e) {
            System.err.println("Error printing box top: " + e.getMessage());
        }
    }

    private static void printBoxDivider(int width) {
        try {
            System.out.println(CYAN + "╠" + "═".repeat(width) + "╣" + RESET);
        } catch (Exception e) {
            System.err.println("Error printing box divider: " + e.getMessage());
        }
    }

    private static void printBoxBottom(int width) {
        try {
            System.out.println(CYAN + "╚" + "═".repeat(width) + "╝" + RESET);
        } catch (Exception e) {
            System.err.println("Error printing box bottom: " + e.getMessage());
        }
    }

    private static void printCentered(String text, int width, String color) {
        try {
            String cleanText = text.replaceAll("\u001B\\[[;\\d]*m", "");
            int padding = (width - cleanText.length()) / 2;
            String line = CYAN + "║" + RESET + " ".repeat(Math.max(0, padding)) +
                    color + text + RESET +
                    " ".repeat(Math.max(0, width - cleanText.length() - padding)) +
                    CYAN + "║" + RESET;
            System.out.println(line);
        } catch (Exception e) {
            System.err.println("Error printing centered text: " + e.getMessage());
        }
    }

    private static String centerLine(String text, int width) {
        try {
            String clean = text.replaceAll("\u001B\\[[;\\d]*m", "");
            if (clean.length() >= width)
                return text;
            int pad = (width - clean.length()) / 2;
            return " ".repeat(Math.max(0, pad)) + text;
        } catch (Exception e) {
            System.err.println("Error centering line: " + e.getMessage());
            return text;
        }
    }

    private static void clearScreen() {
        try {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } catch (Exception e) {
            System.err.println("Error clearing screen: " + e.getMessage());
        }
    }

    private static void pause() {
        try {
            System.out.print(YELLOW + "\nPress ENTER to continue..." + RESET);
            scanner.nextLine();
        } catch (Exception e) {
            System.err.println("Error in pause: " + e.getMessage());
        }
    }

    private static int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print(RED + "[X] Please enter a valid number: " + RESET);
            } catch (Exception e) {
                System.err.println("Error reading integer input: " + e.getMessage());
                System.out.print(RED + "[X] Please enter a valid number: " + RESET);
            }
        }
    }

    private static double getDoubleInput() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print(RED + "[X] Please enter a valid amount: " + RESET);
            } catch (Exception e) {
                System.err.println("Error reading double input: " + e.getMessage());
                System.out.print(RED + "[X] Please enter a valid amount: " + RESET);
            }
        }
    }
}