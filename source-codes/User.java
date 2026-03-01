import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {
    private String username;
    private String password;
    private String email;
    private LocalDateTime createdAt;
    private double monthlyIncome;

    public User(String username, String password, String email, double monthlyIncome) {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be null or empty");
            }
            if (monthlyIncome < 0) {
                throw new IllegalArgumentException("Monthly income cannot be negative");
            }
            
            this.username = username;
            this.password = password; // In real app, this should be hashed
            this.email = email;
            this.createdAt = LocalDateTime.now();
            this.monthlyIncome = monthlyIncome;
        } catch (IllegalArgumentException e) {
            System.err.println("Error creating User: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error creating User: " + e.getMessage());
            throw new RuntimeException("Failed to create user", e);
        }
    }

    // Getters and setters
    public String getUsername() { 
        return username; 
    }
    
    public String getPassword() { 
        return password; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public double getMonthlyIncome() { 
        return monthlyIncome; 
    }
    
    public void setMonthlyIncome(double monthlyIncome) { 
        try {
            if (monthlyIncome < 0) {
                throw new IllegalArgumentException("Monthly income cannot be negative");
            }
            this.monthlyIncome = monthlyIncome;
        } catch (IllegalArgumentException e) {
            System.err.println("Error setting monthly income: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public String toString() {
        try {
            return String.format("User: %s | Email: %s | Monthly Income: $%.2f", 
                               username, email, monthlyIncome);
        } catch (Exception e) {
            System.err.println("Error formatting User string: " + e.getMessage());
            return "User[Error displaying information]";
        }
    }
}