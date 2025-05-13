import java.io.*;
import java.util.HashMap;

public class FileManager {
    // Declares the filenames and folder where user data will be saved.
    private static final String CREDENTIALS_FILE = "users.txt";
    private static final String PERSONAL_DATA_FILE = "personal.txt";
    private static final String TRANSACTIONS_FOLDER = "transactions/";

    //Hashmap because it could be used to store data in memory for faster access
    private static HashMap<String, String> userCache = new HashMap<>();


// saves new user informations
    public static void saveUser(User user) {
        try {
            new File(TRANSACTIONS_FOLDER).mkdirs();
            // in user.txt
            //updateOrAppendLine(String filename, String username, String newData)
            updateOrAppendLine(CREDENTIALS_FILE, user.getUsername(), user.getUsername() + "," + user.getPassword());

            //in personal.txt
            updateOrAppendLine(PERSONAL_DATA_FILE, user.getUsername(),
                user.getUsername() + "," + user.getEmail() + "," + user.getPhone() + "," + user.getAccountNumber() + "," + user.getBalance());

            //in transaction folder
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


// create a file if not present and update the changes of user
    private static void updateOrAppendLine(String filename, String username, String newData) throws IOException {
        File inputFile = new File(filename);
        File tempFile = new File("temp_" + filename);
    
    //  Ensure file exists (if file not exists, it create a new one)
        if (!inputFile.exists()) {
            inputFile.createNewFile();
        }
    
        boolean found = false;
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        String line;
    // ensure only current user data is modified
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
        //for user.txt file
        try {
            BufferedReader credReader = new BufferedReader(new FileReader(CREDENTIALS_FILE));

            String line;
            String password = null;

        //check username and password while login
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

            //for personal.txt file
            BufferedReader personalReader = new BufferedReader(new FileReader(PERSONAL_DATA_FILE));
            // default value in buffer
            String email = "", phone = "", accountNumber = "";
            double balance = 0;

            while ((line = personalReader.readLine()) != null) {
                String[] parts = line.split(",");
                //check only user info will get change
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

            //for transaction folder
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
