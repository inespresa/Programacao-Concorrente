
import java.util.concurrent.Semaphore;

class Barreira {
    private final int N;
    private int c = 0;
    private boolean w = false;

    public Barreira(int N) {
        this.N = N;
    }

    public synchronized void await() throws InterruptedException {
        c += 1;

        if (c == 1)
            w = true;

        if (c == N) {
            notifyAll();
            c = 0;
            w = false;
        }
        while (w)
            wait();
    }
}
