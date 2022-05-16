
class Main {
    public static void main(String[] args) throws InterruptedException {
        final int N = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);

        Counter c = new Counter();

        Thread a[] = new Thread[N];

        for (int i = 0; i < N; i++) {
            a[i] = new Incrementer(I, c);
            a[i].start();
        }
        for (int i = 0; i < N; i++) {
            a[i].join();
        }
        System.out.println(c.value());

    }

}

class Incrementer extends Thread {
    Counter c;
    int iterations;

    Incrementer(int iterations, Counter c) {
        this.iterations = iterations;
        this.c = c;
    }

    public void run() {
        for (int i = 0; i < iterations; i++) {
            c.increment();
        }
    }
}

class Counter {
    int value;

    synchronized void increment() {
        value += 1;
    }

    synchronized int value() {
        return value;
    }
}