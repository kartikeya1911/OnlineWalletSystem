import java.util.LinkedList;
import java.util.UUID;

public class User {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String accountNumber;
    private double balance;
    private LinkedList<String> transactions = new LinkedList<>();

    public User(String username, String password, String email, String phone, double balance) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.accountNumber = UUID.randomUUID().toString().substring(0, 10);
        this.balance = balance;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }

    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setBalance(double balance) { this.balance = balance; }

    //stores only 10 transactions in txt file
    public void addTransaction(String detail) {
        if (transactions.size() == 10) transactions.removeFirst();
        transactions.add(detail);
    }

    public LinkedList<String> getLast10Transactions() {
        return transactions;
    }
}

