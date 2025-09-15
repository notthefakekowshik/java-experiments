package com.kowshik;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// A simple POJO to represent a user
class BankUser {
    private String userId;
    private String name;

    public BankUser(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
}

/**
 * A shared context class to manage thread-local data for a banking application.
 */
class TransactionContext {

    // ThreadLocal for storing the current user object
    private static final ThreadLocal<BankUser> currentUser = new ThreadLocal<>();

    // ThreadLocal for storing the unique transaction ID
    private static final ThreadLocal<String> transactionId = new ThreadLocal<>();

    public static void setCurrentUser(BankUser user) {
        currentUser.set(user);
    }

    public static BankUser getCurrentUser() {
        return currentUser.get();
    }

    public static void setTransactionId(String id) {
        transactionId.set(id);
    }

    public static String getTransactionId() {
        return transactionId.get();
    }

    // Removes current thread from the map. Crucial cleanup method to prevent memory leaks in a thread pool environment
    /*
        How does it lead to memory leak?
        Let's say Alice has completed the transaction
     */
    public static void clear() {
        currentUser.remove();
        transactionId.remove();
    }
}

/**
 * Simulates a plain Java banking application using ThreadLocal.
 */
public class ThreadLocalDemo {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Simulate two concurrent users initiating transactions
        executor.submit(() -> processTransaction(new BankUser("user123", "Alice")));
        executor.submit(() -> processTransaction(new BankUser("user456", "Bob")));

        executor.shutdown();
    }

    private static void processTransaction(BankUser user) {
        // Generate a unique transaction ID
        String currentTransactionId = "TX-" + UUID.randomUUID().toString();

        try {
            // Set the thread-local context at the start of the "request"
            TransactionContext.setCurrentUser(user);
            TransactionContext.setTransactionId(currentTransactionId);

            System.out.println(Thread.currentThread().getName() + " -> Starting transaction " + currentTransactionId + " for user " + user.getName());

            // Perform business logic without passing user or transaction ID as arguments
            BankService.executeTransfer();

        } finally {
            // Always clean up the thread-local context when done
            System.out.println(Thread.currentThread().getName() + " -> Ending transaction " + TransactionContext.getTransactionId());
            TransactionContext.clear();
        }
    }
}

/**
 * A service layer class that performs a bank transfer.
 * It accesses the thread-local context directly.
 */
class BankService {
    public static void executeTransfer() {
        // Access the transaction ID and user from the thread-local context
        String txId = TransactionContext.getTransactionId();
        BankUser user = TransactionContext.getCurrentUser();

        System.out.println(Thread.currentThread().getName() + " -> BankService: Executing transfer for user " + user.getName() + " (ID: " + user.getUserId() + ") with transaction ID " + txId);

        // Simulate a call to another layer, like a repository or fraud detection service
        FraudService.checkTransaction();
    }
}

/**
 * A hypothetical fraud detection service.
 * It also accesses the thread-local context.
 */
class FraudService {
    public static void checkTransaction() {
        // Access the transaction ID and user from the thread-local context
        String txId = TransactionContext.getTransactionId();
        BankUser user = TransactionContext.getCurrentUser();

        System.out.println(Thread.currentThread().getName() + " -> FraudService: Checking for fraud for user " + user.getName() + " and transaction " + txId);

        // Simulate fraud check logic
        System.out.println(Thread.currentThread().getName() + " -> FraudService: Check passed.");
    }
}