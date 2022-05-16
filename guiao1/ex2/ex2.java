// Modifique o programa para as N threads terem acesso a um único objecto partilhado, 
//de uma classe Counter. Cada thread deverá agora, em vez de imprimir números, 
//incrementar I vezes o contador. 

//VERSÂO 1: cada thread invoca um método increment da classe Counter.

/* class Main {
    public static void main(String[] args) throws InterruptedException {
        final int N = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);

        Counter c = new Counter();
        Thread[] a = new Thread[N];

        for (int i = 0; i < N; i++) {
            a[i] = new Incrementer(c, I);
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

    Incrementer(Counter c, int iterations) {
        this.c = c;
        this.iterations = iterations;
    }

    public void run() {
        for (int i = 0; i < iterations; i++)
            c.increment();
    }
}

class Counter {
    private int c;

    void increment() {
        c += 1;
    }

    int value() {
        return c;
    }
} */

//VERSÃO 2: as threadas acedem directamente a uma variável de instância

class Main {
    public static void main(String[] args) throws InterruptedException {
        final int N = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);

        Thread[] a = new Thread[N];
        Counter c = new Counter();

        for (int i = 0; i < N; i++) {
            a[i] = new Incrementer(I, c);
            a[i].start();
        }
        for (int i = 0; i < N; i++) {
            a[i].join();
        }

        System.out.println(c.value);

    }
}

class Incrementer extends Thread {
    int iterations;
    Counter c;

    Incrementer(int iterations, Counter c) {
        this.iterations = iterations;
        this.c = c;
    }

    public void run() {
        for (int i = 0; i < iterations; i++) {
            c.value += 1;
        }
    }

}

class Counter {
    public int value;
}