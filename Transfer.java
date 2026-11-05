/* Name: Serge Keou Ketchiozo
   Course: CNT 4714 Fall 2026
   Assignment title: Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
   Due Date: October 11, 2026
*/

import java.security.SecureRandom;

public class Transfer implements Runnable {
    private static final SecureRandom rand = new SecureRandom();
    private final String agentName;
    private final ABankAccount[] accounts;

    public Transfer(String name, ABankAccount[] accounts) {
        this.agentName = name;
        this.accounts = accounts;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Sleep: 150ms - 350ms
                Thread.sleep(rand.nextInt(201) + 150);

                int fromIndex = rand.nextInt(accounts.length);
                int toIndex = (fromIndex + 1) % accounts.length;
                int amount = rand.nextInt(99) + 1;

                ABankAccount.executeTransfer(accounts[fromIndex], accounts[toIndex], amount, agentName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}