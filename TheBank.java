/* Name: Serge Keou Ketchiozo
   Course: CNT 4714 Fall 2026
   Assignment title: Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
   Due Date: October 11, 2026
*/

public interface TheBank {
    void deposit(int value, String acctNum, String agentName);
    void withdrawal(int value, String acctNum, String agentName);
    int getBalance();
}