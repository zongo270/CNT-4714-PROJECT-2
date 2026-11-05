/* Name: Serge Keou Ketchiozo
   Course: CNT 4714 Fall 2026
   Assignment title: Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
   Due Date: October 11, 2026
*/

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ABankingSimulator {
    public static void main(String[] args) {
        // Clear previous transactions.tsv log file
        File tsvFile = new File("transactions.tsv");
        if (tsvFile.exists()) {
            tsvFile.delete();
        }

        // Two shared bank accounts initialized to $0
        ABankAccount[] accounts = new ABankAccount[]{
                new ABankAccount("JA-1", 0),
                new ABankAccount("JA-2", 0)
        };

        // Output Header
        System.out.println("Deposit Agents\t\t\tWithdrawal Agents\t\t\tBalances\t\tTransaction Number");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        // 19 threads total: 5 Depositors, 10 Withdrawals, 2 Transfers, 1 Internal Auditor, 1 Treasury Agent
        ExecutorService executor = Executors.newFixedThreadPool(19);

        // Deploy 5 Depositor Agents (DT0 - DT4)
        for (int i = 0; i < 5; i++) {
            executor.execute(new Depositor("Agent DT" + i, accounts));
        }

        // Deploy 10 Withdrawal Agents (WT0 - WT9)
        for (int i = 0; i < 10; i++) {
            executor.execute(new Withdrawal("Agent WT" + i, accounts));
        }

        // Deploy 2 Transfer Agents (TR0, TR1)
        for (int i = 0; i < 2; i++) {
            executor.execute(new Transfer("Agent TR" + i, accounts));
        }

        // Deploy 1 Internal Auditor Agent
        executor.execute(new InternalAuditor(accounts));

        // Deploy 1 US Treasury Agent
        executor.execute(new TreasuryDept(accounts));

        executor.shutdown();
    }
}