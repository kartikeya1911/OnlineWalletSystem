import java.util.UUID;
import java.util.HashSet;

public class TransactionService {
    private static HashSet<String> usedTxnIds = new HashSet<>(); // 3. HashSet

    public static boolean transferMoney(User sender, String recipientName, double amount) {
        User recipient = FileManager.loadUser(recipientName);
        if (recipient == null) {
            System.out.println("\nRecipient not found.");
            return false;
        }
        if (sender.getBalance() < amount) {
            System.out.println("\nInsufficient balance.");
            return false;
        }

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        String txnId;
        do {
            txnId = UUID.randomUUID().toString().substring(0, 8);
        } while (usedTxnIds.contains(txnId));

        usedTxnIds.add(txnId);

        String senderDetail = "[TxnID: " + txnId + "] Sent Rs." + amount + " to " + recipientName;
        String recipientDetail = "[TxnID: " + txnId + "] Received Rs." + amount + " from " + sender.getUsername();

        sender.addTransaction(senderDetail);
        recipient.addTransaction(recipientDetail);

        FileManager.saveUser(sender);
        FileManager.saveUser(recipient);

        System.out.println("\n********** Transfer successful **********");
        return true;
    }
}