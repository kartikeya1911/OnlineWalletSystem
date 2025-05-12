import java.util.Scanner;
import java.util.UUID;
import java.util.Stack;

public class OnlineWalletSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;
    private static Stack<String> logHistory = new Stack<>(); // 4. Stack

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n********** Welcome to the Simple Wallet System **********");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose: ");
            int choice = scanner.nextInt(); scanner.nextLine();

            if (choice == 1) register();
            else if (choice == 2) login();
            else break;
        }
    }

    private static void register() {
        System.out.println("\n********** Registration **********");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        System.out.print("Initial balance: ");
        double balance = scanner.nextDouble(); scanner.nextLine();

        currentUser = new User(username, password, email, phone, balance);
        FileManager.saveUser(currentUser);
        System.out.println("\nRegistered and logged in successfully!");
        dashboard();
    }

    private static void login() {
        System.out.println("\n********** Login **********");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        User user = FileManager.loadUser(username);
        if (user == null) return;

        System.out.print("Password: ");
        String password = scanner.nextLine();
        if (user.getPassword().equals(password)) {
            currentUser = user;
            System.out.println("\nLogin successful!");
            dashboard();
        } else {
            System.out.println("\nIncorrect password.");
        }
    }

    private static void dashboard() {
        while (true) {
            System.out.println("\n********** Dashboard **********");
            System.out.println("Welcome, " + currentUser.getUsername());
            System.out.println("Account Number: " + currentUser.getAccountNumber());
            System.out.println("1. View Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. View Last 10 Transactions");
            System.out.println("4. Transfer Money");
            System.out.println("5. Update Profile");
            System.out.println("6. Logout");
            System.out.print("Choose: ");
            int choice = scanner.nextInt(); scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("\nCurrent Balance: Rs." + currentUser.getBalance());
                    break;
                case 2:
                    System.out.print("Enter deposit amount: ");
                    double deposit = scanner.nextDouble(); scanner.nextLine();
                    if (deposit > 0) {
                        currentUser.setBalance(currentUser.getBalance() + deposit);
                        String txnId = UUID.randomUUID().toString().substring(0, 8);
                        currentUser.addTransaction("[TxnID: " + txnId + "] Deposited Rs." + deposit);
                        FileManager.saveUser(currentUser);
                        System.out.println("\nDeposit successful.");
                    } else {
                        System.out.println("\nInvalid deposit amount.");
                    }
                    break;
                case 3:
                    System.out.println("\n********** Last 10 Transactions **********");
                    for (String txn : currentUser.getLast10Transactions()) {
                        System.out.println(txn);
                    }
                    break;
                case 4:
                    System.out.print("Enter recipient username: ");
                    String recipient = scanner.nextLine();
                    System.out.print("Enter amount to transfer: ");
                    double transferAmt = scanner.nextDouble(); scanner.nextLine();
                    TransactionService.transferMoney(currentUser, recipient, transferAmt);
                    break;
                case 5:
                    updateProfile();
                    break;
                case 6:
                    FileManager.saveUser(currentUser);
                    logHistory.push("Logout by: " + currentUser.getUsername());
                    System.out.println("\nLogged out. Have a nice day!");
                    return;
            }
        }
    }

    private static void updateProfile() {
        System.out.println("\n********** Update Profile **********");
        System.out.println("1. Change Email");
        System.out.println("2. Change Phone Number");
        System.out.println("3. Change Password");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt(); scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("New email: ");
                currentUser.setEmail(scanner.nextLine());
                break;
            case 2:
                System.out.print("New phone number: ");
                currentUser.setPhone(scanner.nextLine());
                break;
            case 3:
                System.out.print("New password: ");
                currentUser.setPassword(scanner.nextLine());
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        FileManager.saveUser(currentUser);
        System.out.println("Profile updated successfully.");
    }
}
