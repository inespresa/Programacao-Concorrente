
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

    synchronized void deposit(int id, int val) throws InvalidAccount {
        if (id < 0 || id > N)
            throw new InvalidAccount();
        accounts[id].deposit(val);
    }

    synchronized void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds {
        if (id < 0 || id >= N)
            throw new InvalidAccount();
        accounts[id].withdraw(val);
    }

    synchronized int totalBalance(int accounts[]) throws InvalidAccount {
        int total = 0;
        for (int id : accounts) {
            if (id < 0 || id >= N)
                throw new InvalidAccount();
            total += this.accounts[id].balance();
        }
        return total;
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
    public static void main(String[] args) throws InterruptedException, InvalidAccount {
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
            System.out.println(b.accounts[i].balance());
    }
}