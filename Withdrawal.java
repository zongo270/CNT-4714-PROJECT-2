/* Name: Serge Keou Ketchiozo
   Course: CNT 4714 Fall 2026
   Assignment title: Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
   Due Date: October 11, 2026
*/

import java.security.SecureRandom;

public class Withdrawal implements Runnable {
    private static final SecureRandom rand = new SecureRandom();
    private final String agentName;
    private final ABankAccount[] accounts;

    public Withdrawal(String name, ABankAccount[] accounts) {
        this.agentName = name;
        this.accounts = accounts;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Sleep: 0ms - 40ms (withdrawals execute frequently)
                Thread.sleep(rand.nextInt(41));

                int accountIndex = rand.nextInt(accounts.length);
                int amount = rand.nextInt(99) + 1; // $1 to $99

                accounts[accountIndex].withdrawal(amount, accounts[accountIndex].getAccountName(), agentName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}