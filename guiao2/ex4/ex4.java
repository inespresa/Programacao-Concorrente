class NotEnoughFunds extends Exception {
}

class InvalidAccount extends Exception {
}

class Bank {
    static class Account {
        private int balance;

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
    }

    Account accounts[];
    private int N;

    Bank(int N) {
        this.N = N;
        accounts = new Account[N];
        for (int i = 0; i < N; i++) {
            accounts[i] = new Account();
        }
    }

    private Account get(int id) throws InvalidAccount {
        if (id < 0 || id > N)
            throw new InvalidAccount();
        return accounts[id];
    }

    void deposit(int id, int val) throws InvalidAccount {
        Account c = get(id);
        synchronized (c) {
            c.deposit(val);
        }
    }

    void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds {
        Account c = get(id);
        synchronized (c) {
            c.withdraw(val);
        }
    }

    int totalBalance(int accounts[]) throws InvalidAccount {
        int total = 0;
        for (int id : accounts) {
            Account c = get(id);
            synchronized (c) {
                total += c.balance();
            }
        }
        return total;
    }

    synchronized void transfer(int from, int to, int amount) throws InvalidAccount, NotEnoughFunds {
        Account c1 = get(from);
        Account c2 = get(to);
        synchronized (c1) {
            synchronized (c2) {
                c1.withdraw(amount);
                c2.deposit(amount);
            }
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

class Main {
    public static void main(String[] args) throws InterruptedException, InvalidAccount, NotEnoughFunds {
        final int NC = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);
        final int N = Integer.parseInt(args[2]);

        Bank b = new Bank(NC);

        Thread a[] = new Thread[N];

        for (int i = 0; i < N; i++) {
            a[i] = new Depositor(b, I);
            a[i].start();
        }
        for (int i = 0; i < N; i++) {
            a[i].join();
        }

        for (int i = 0; i < NC; i++)
            System.out.println(i + " - " + b.accounts[i].balance());

        b.transfer(3, 0, 150);

        for (int i = 0; i < NC; i++)
            System.out.println(i + " - " + b.accounts[i].balance());
    }
}