/* Name: Serge Keou Ketchiozo
   Course: CNT 4714 Fall 2026
   Assignment title: Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
   Due Date: October 11, 2026
*/

import java.security.SecureRandom;

public class Depositor implements Runnable {
    private static final SecureRandom rand = new SecureRandom();
    private final String agentName;
    private final ABankAccount[] accounts;

    public Depositor(String name, ABankAccount[] accounts) {
        this.agentName = name;
        this.accounts = accounts;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Sleep: 100ms - 250ms (keeps deposit rate lower than withdrawal drain)
                Thread.sleep(rand.nextInt(151) + 100);

                int accountIndex = rand.nextInt(accounts.length);
                int amount = rand.nextInt(600) + 1; // $1 to $600

                accounts[accountIndex].deposit(amount, accounts[accountIndex].getAccountName(), agentName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}