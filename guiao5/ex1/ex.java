package ex1;

import java.util.concurrent.Semaphore;
import java.util.Random;

class BoundedBuffer {
    private int[] buf;
    private int iget = 0;
    private int iput = 0;
    private int nelems = 0;

    BoundedBuffer(int N) {
        this.buf = new int[N];

    }

    public synchronized int get() throws InterruptedException {
        while (!(nelems == 0))
            wait();
        int res;
        res = buf[iget];
        iget = (iget + 1) % buf.length;
        notifyAll();
        nelems -= 1;
        // notify(); // <- errado
        return res;
    }

    public synchronized void put(int v) throws InterruptedException {
        while (!(nelems == 0))
            wait();
        buf[iput] = v;
        iput = (iput + 1) % buf.length;
        nelems += 1;
        notifyAll();
    }

}

class Teste {
    public static void main(String[] args) {

        BoundedBuffer b = new BoundedBuffer(20);

        new Thread(() -> {
            try {
                for (int i = 1;; i++) {
                    System.out.println("Vou fazer put de: " + i);
                    b.put(i);
                    System.out.println("Fiz put de: " + i);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
            }

        }).start();

        new Thread(() -> {
            try {
                for (int i = 1;; i++) {
                    System.out.println("Vou fazer get");
                    int x = b.get();
                    System.out.println("Fiz get de: " + x);
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
            }

        }).start();

    }
}