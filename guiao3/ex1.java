
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class NotEnoughFunds extends Exception {
}

class InvalidAccount extends Exception {
}

class Bank {
    static class Account {
        private int balance;

        Account(int balance) {
            this.balance = balance;
        }

        public void deposit(int val) {
            balance += val;
        }

        public void withdraw(int val) throws NotEnoughFunds {
            if (balance < val)
                throw new NotEnoughFunds();
            balance -= val;
        }

        public int balance() {
            return balance;
        }

        Lock l = new ReentrantLock();
    }

    Lock l = new ReentrantLock();
    HashMap<Integer, Account> accounts = new HashMap<>();
    private int lastId = 0;

    private Account get(int id) throws InvalidAccount {
        if (id < 0 || id >= lastId)
            throw new InvalidAccount();
        return accounts.get(id);
    }

    void deposit(int id, int val) throws InvalidAccount {
        Account c;
        l.lock();
        try {
            c = get(id);
            c.l.lock();
        } finally {
            l.unlock();
        }
        try {
            c.deposit(val);
        } finally {
            c.l.unlock();
        }
    }

    void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds {
        Account c;
        l.lock();
        try {
            c = get(id);
            c.l.lock();
        } finally {
            l.unlock();
        }
        try {
            c.withdraw(val);
        } finally {
            c.l.unlock();
        }
    }

    int totalBalance(int accounts[]) throws InvalidAccount {

        accounts = accounts.clone();
        Arrays.sort(accounts);

        Account[] arrAccounts = new Account[accounts.length];
        int total = 0;

        l.lock();

        try {
            for (int i = 0; i < accounts.length; i++) {
                arrAccounts[i] = get(i);
            }
            for (Account a : arrAccounts) {
                a.l.lock();
            }
        } finally {
            l.unlock();
        }

        for (Account a : arrAccounts) {
            total += a.balance();
            a.l.unlock();
        }

        return total;
    }

    void transfer(int from, int to, int amount) throws InvalidAccount, NotEnoughFunds {
        Account c1, c2, cfrom, cto;

        l.lock();

        try {
            c1 = get(Math.min(from, to));
            c2 = get(Math.max(from, to));

            cfrom = get(from);
            cto = get(to);

            c1.l.lock();
            c2.l.lock();
        } finally {
            l.unlock();
        }

        try {
            cfrom.withdraw(amount);
            cto.deposit(amount);
        } finally {
            c1.l.unlock();
            c2.l.unlock();
        }
    }

    int createAccount(int initialBalance) {
        l.lock();

        try {
            accounts.put(lastId++, new Account(initialBalance));
        } finally {
            l.unlock();
        }
        return lastId - 1;
    }

    int closeAccount(int id) throws InvalidAccount {
        Account a;
        l.lock();

        try {
            a = get(id);
            accounts.remove(id);
            a.l.lock();
        } finally {
            l.unlock();
        }
        try {
            return a.balance();
        } finally {
            a.l.unlock();
        }

    }

}

class Depositor extends Thread {
    private Bank b;
    private int iterations;

    Depositor(Bank b, int iterations) {
        this.b = b;
        this.iterations = iterations;
    }

    public void run() {
        try {
            for (int i = 0; i < iterations; i++) {
                b.deposit(i, i * 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Falta corrigir:
/*
 * class Main {
 * public static void main(String[] args) throws InterruptedException,
 * InvalidAccount, NotEnoughFunds {
 * final int NC = Integer.parseInt(args[0]);
 * final int I = Integer.parseInt(args[1]);
 * final int N = Integer.parseInt(args[2]);
 * 
 * Bank b = new Bank();
 * 
 * Thread a[] = new Thread[N];
 * 
 * for (int i = 0; i < N; i++) {
 * a[i] = new Depositor(b, I);
 * a[i].start();
 * }
 * for (int i = 0; i < N; i++) {
 * a[i].join();
 * }
 * 
 * for (int i = 0; i < NC; i++)
 * System.out.println(i + " - " + b.accounts[i].balance());
 * 
 * b.transfer(3, 0, 150);
 * 
 * for (int i = 0; i < NC; i++)
 * System.out.println(i + " - " + b.accounts[i].balance());
 * }
 * }
 */