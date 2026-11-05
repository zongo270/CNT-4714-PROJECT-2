/* Name: Serge Keou Ketchiozo
   Course: CNT 4714 Fall 2026
   Assignment title: Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
   Due Date: October 11, 2026
*/

import java.security.SecureRandom;

public class TreasuryDept implements Runnable {
    private static final SecureRandom rand = new SecureRandom();
    private final ABankAccount[] accounts;
    private int lastAuditTx = 0;

    public TreasuryDept(ABankAccount[] accounts) {
        this.accounts = accounts;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Sleep: 700ms - 1300ms (longest average sleep time)
                Thread.sleep(rand.nextInt(601) + 700);

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
                                System.out.println("UNITED STATES DEPARTMENT OF THE TREASURY - Bank Audit Beginning...");
                                System.out.printf("The total number of transactions since last Treasury Department audit is: %d\n", txCount);
                                for (ABankAccount acct : accounts) {
                                    System.out.printf("TREASURY DEPT AUDTIOR FINDS CURRENT ACCOUNT BALANCE FOR %s TO BE: $%d\n",
                                            acct.getAccountName(), acct.getBalance());
                                }
                                System.out.println("UNITED STATES DEPARTMENT OF THE TREASURY - Bank Audit Terminated...");
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