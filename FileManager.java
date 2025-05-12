import java.io.*;
import java.util.HashMap;

public class FileManager {
    private static final String CREDENTIALS_FILE = "users.txt";
    private static final String PERSONAL_DATA_FILE = "personal.txt";
    private static final String TRANSACTIONS_FOLDER = "transactions/";

    private static HashMap<String, String> userCache = new HashMap<>(); // 2. HashMap

    public static void saveUser(User user) {
        try {
            new File(TRANSACTIONS_FOLDER).mkdirs();

            updateOrAppendLine(CREDENTIALS_FILE, user.getUsername(), user.getUsername() + "," + user.getPassword());
            updateOrAppendLine(PERSONAL_DATA_FILE, user.getUsername(),
                user.getUsername() + "," + user.getEmail() + "," + user.getPhone() + "," + user.getAccountNumber() + "," + user.getBalance());

            BufferedWriter txnWriter = new BufferedWriter(new FileWriter(TRANSACTIONS_FOLDER + user.getUsername() + ".txt"));
            for (String txn : user.getLast10Transactions()) {
                txnWriter.write(txn);
                txnWriter.newLine();
            }
            txnWriter.close();
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }

    private static void updateOrAppendLine(String filename, String username, String newData) throws IOException {
        File inputFile = new File(filename);
        File tempFile = new File("temp_" + filename);
    
        //  Ensure file exists
        if (!inputFile.exists()) {
            inputFile.createNewFile();
        }
    
        boolean found = false;
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        String line;
    
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(username + ",")) {
                writer.write(newData);
                writer.newLine();
                found = true;
            } else {
                writer.write(line);
                writer.newLine();
            }
        }
    
        if (!found) {
            writer.write(newData);
            writer.newLine();
        }
    
        reader.close();
        writer.close();
    
        // Delete old and rename new
        if (!inputFile.delete()) {
            System.out.println("Could not delete old file: " + filename);
        }
        if (!tempFile.renameTo(inputFile)) {
            System.out.println("Could not rename temp file to: " + filename);
        }
    }
    

    public static User loadUser(String username) {
        try {
            BufferedReader credReader = new BufferedReader(new FileReader(CREDENTIALS_FILE));
            String line;
            String password = null;
            while ((line = credReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    password = parts[1];
                    break;
                }
            }
            credReader.close();

            if (password == null) {
                System.out.println("User not found.");
                return null;
            }

            BufferedReader personalReader = new BufferedReader(new FileReader(PERSONAL_DATA_FILE));
            String email = "", phone = "", accountNumber = "";
            double balance = 0;
            while ((line = personalReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    email = parts[1];
                    phone = parts[2];
                    accountNumber = parts[3];
                    balance = Double.parseDouble(parts[4]);
                    break;
                }
            }
            personalReader.close();

            User user = new User(username, password, email, phone, balance);

            File txnFile = new File(TRANSACTIONS_FOLDER + username + ".txt");
            if (txnFile.exists()) {
                BufferedReader txnReader = new BufferedReader(new FileReader(txnFile));
                while ((line = txnReader.readLine()) != null) {
                    user.addTransaction(line);
                }
                txnReader.close();
            }

            return user;
        } catch (IOException e) {
            System.out.println("Error loading user data: " + e.getMessage());
            return null;
        }
    }
}
