import java.util.concurrent.Semaphore;

class BoundedBuffer<T> {
    T buffer[];
    int iget = 0;
    int iput = 0;

    Semaphore items;
    Semaphore slots;
    Semaphore mutget = new Semaphore(1); // para a exclusão mútua do get
    Semaphore mutput = new Semaphore(1); // para a exclusão mútua do put

    public BoundedBuffer(int N) {
        buffer = (T[]) new Object[N];
        items = new Semaphore(0);
        slots = new Semaphore(N);
    }

    public T get() throws InterruptedException {
        items.acquire(); // para remover um item
        mutget.acquire();
        T res = buffer[iget];
        iget = (iget + 1) % buffer.length;
        mutget.release();
        slots.release(); // porque removi um item, fiquei com um slot disponível
        return res;
    }

    void put(T x) throws InterruptedException {
        slots.acquire(); // simetricamente ao get(), este adquire um slot
        mutput.acquire();
        buffer[iput] = x;
        iput = (iput + 1) % buffer.length;
        mutput.release();
        items.release(); // liberta um item
    }
}

class Main {
    public static void main(String[] args) throws InterruptedException {
        BoundedBuffer<Integer> b = new BoundedBuffer(20);

        new Thread(() -> {
            try {
                for (int i = 1;; ++i) {
                    System.out.println("vou fazer put de " + i);
                    b.put(i);
                    System.out.println("fiz put de " + i);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
            }
        }).start();

        new Thread(() -> {
            try {
                while (true) {
                    System.out.println("vou fazer get");
                    int v = b.get();
                    System.out.println("get retornou " + v);
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
            }
        }).start();
    }
}