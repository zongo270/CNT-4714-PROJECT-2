/* Name: Serge Keou Ketchiozo
   Course: CNT 4714 Fall 2026
   Assignment title: Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
   Due Date: October 11, 2026
*/

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ABankAccount implements TheBank {
    private final String accountName;
    private int balance = 0;
    
    // Explicit ReentrantLock (no fairness policy) and Condition variable
    private final ReentrantLock accountLock = new ReentrantLock(false);
    private final Condition canWithdraw = accountLock.newCondition();

    // Global transaction sequence counter
    private static int transactionNum = 0;
    private static final Object fileLock = new Object();

    public ABankAccount(String accountName, int initialBalance) {
        this.accountName = accountName;
        this.balance = initialBalance;
    }

    public ReentrantLock getLock() {
        return accountLock;
    }

    public String getAccountName() {
        return accountName;
    }

    @Override
    public int getBalance() {
        return balance;
    }

    @Override
    public void deposit(int value, String acctNum, String agentName) {
        accountLock.lock();
        try {
            balance += value;
            transactionNum++;
            int currentTx = transactionNum;

            // Display in the Deposit Agents column
            System.out.printf("%-30s\t\t\t(+) %s balance is $%d\t\t%d\n",
                    agentName + " deposits $" + value + " into: " + acctNum,
                    acctNum, balance, currentTx);

            // Flag deposits > $450
            if (value > 450) {
                System.out.printf("\n***Flagged Transaction*** %s Made A Deposit In Excess Of $450.00 USD - See Flagged Transaction Log.\n\n", agentName);
                logFlaggedTransaction(agentName, "deposit", value, currentTx);
            }

            // Signal all waiting withdrawal threads on this account
            canWithdraw.signalAll();
        } finally {
            accountLock.unlock();
        }
    }

    @Override
    public void withdrawal(int value, String acctNum, String agentName) {
        accountLock.lock();
        try {
            // Block while account has insufficient funds
            while (value > balance) {
                System.out.printf("%s attempts to withdraw $%d from %s (******) WITHDRAWAL BLOCKED - INSUFFICIENT FUNDS!!! Balance only $%d\n",
                        agentName, value, acctNum, balance);
                try {
                    canWithdraw.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            // Execute withdrawal
            balance -= value;
            transactionNum++;
            int currentTx = transactionNum;

            // Display in the Withdrawal Agents column
            System.out.printf("\t\t\t%-30s(-) %s balance is $%d\t\t%d\n",
                    agentName + " withdraws $" + value + " from " + acctNum,
                    acctNum, balance, currentTx);

            // Flag withdrawals > $90
            if (value > 90) {
                System.out.printf("\n***Flagged Transaction*** %s Made A Withdrawal In Excess Of $90.00 USD - See Flagged Transaction Log.\n\n", agentName);
                logFlaggedTransaction(agentName, "withdrawal", value, currentTx);
            }
        } finally {
            accountLock.unlock();
        }
    }

    // Atomic Transfer requiring locks on both accounts simultaneously
    public static void executeTransfer(ABankAccount fromAcct, ABankAccount toAcct, int amount, String agentName) {
        // Enforce lock acquisition order to avoid deadlock
        ABankAccount first = fromAcct.getAccountName().compareTo(toAcct.getAccountName()) < 0 ? fromAcct : toAcct;
        ABankAccount second = (first == fromAcct) ? toAcct : fromAcct;

        if (first.getLock().tryLock()) {
            try {
                if (second.getLock().tryLock()) {
                    try {
                        if (fromAcct.balance >= amount) {
                            fromAcct.balance -= amount;
                            toAcct.balance += amount;
                            transactionNum++;
                            int currentTx = transactionNum;

                            System.out.printf("TRANSFER -> %s transferring $%d from %s to %s\t%s balance is now $%d\t%d\n",
                                    agentName, amount, fromAcct.getAccountName(), toAcct.getAccountName(),
                                    fromAcct.getAccountName(), fromAcct.getBalance(), currentTx);
                            System.out.printf("TRANSFER COMPLETE - Account %s balance now $%d\n",
                                    toAcct.getAccountName(), toAcct.getBalance());

                            // Signal waiting threads on destination account
                            toAcct.canWithdraw.signalAll();
                        }
                    } finally {
                        second.getLock().unlock();
                    }
                }
            } finally {
                first.getLock().unlock();
            }
        }
    }

    // TSV file output matching assignment structure
    private static void logFlaggedTransaction(String agentName, String type, int amount, int txNumber) {
        synchronized (fileLock) {
            try (FileWriter fw = new FileWriter("transactions.tsv", true);
                 PrintWriter pw = new PrintWriter(fw)) {
                
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss z");
                String timestamp = ZonedDateTime.now().format(dtf);

                pw.printf("%s issued %s of:\t$%d.00 at:\t%s\tTransaction Number: %d\n",
                        agentName, type, amount, timestamp, txNumber);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getTransactionCount() {
        return transactionNum;
    }
}