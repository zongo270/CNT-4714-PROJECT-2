/* Name: Serge Keou Ketchiozo
   Course: CNT 4714 Fall 2026
   Assignment title: Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
   Due Date: October 11, 2026
*/

import java.security.SecureRandom;

public class InternalAuditor implements Runnable {
    private static final SecureRandom rand = new SecureRandom();
    private final ABankAccount[] accounts;
    private int lastAuditTx = 0;

    public InternalAuditor(ABankAccount[] accounts) {
        this.accounts = accounts;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Sleep: 400ms - 800ms (runs periodically and less frequently)
                Thread.sleep(rand.nextInt(401) + 400);

                ABankAccount first = accounts[0].getAccountName().compareTo(accounts[1].getAccountName()) < 0 ? accounts[0] : accounts[1];
                ABankAccount second = (first == accounts[0]) ? accounts[1] : accounts[0];

                if (first.getLock().tryLock()) {
                    try {
                        if (second.getLock().tryLock()) {
                            try {
                                int currentTotalTx = ABankAccount.getTransactionCount();
                                int txCount = currentTotalTx - lastAuditTx;
                                lastAuditTx = currentTotalTx;

                                System.out.println("************************************************************************************************");
                                System.out.println("Internal Bank Audit Beginning...");
                                System.out.printf("The total number of transactions since the last Internal audit is: %d\n", txCount);
                                for (ABankAccount acct : accounts) {
                                    System.out.printf("INTERNAL BANK AUDTIOR FINDS CURRENT ACCOUNT BALANCE FOR %s TO BE: $%d\n",
                                            acct.getAccountName(), acct.getBalance());
                                }
                                System.out.println("Internal Bank Audit Complete.");
                                System.out.println("************************************************************************************************\n");
                            } finally {
                                second.getLock().unlock();
                            }
                        }
                    } finally {
                        first.getLock().unlock();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}