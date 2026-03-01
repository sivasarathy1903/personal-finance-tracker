import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, User> users;
    private User currentUser;

    public UserManager() {
        this.users = new HashMap<>();
    }

    public boolean registerUser(String username, String password, String email, double monthlyIncome) {
        if (users.containsKey(username)) {
            return false; // User already exists
        }
        
        if (!isValidEmail(email)) {
            return false; // Invalid email
        }
        
        User newUser = new User(username, password, email, monthlyIncome);
        users.put(username, newUser);
        DataStorage.saveUser(newUser);
        return true;
    }

    public boolean loginUser(String username, String password) {
        User user = DataStorage.loadUser(username);
        if (user != null && user.getPassword().equals(password)) {
            this.currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public boolean updateUserIncome(double newIncome) {
        if (currentUser != null && newIncome > 0) {
            currentUser.setMonthlyIncome(newIncome);
            DataStorage.saveUser(currentUser);
            return true;
        }
        return false;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}