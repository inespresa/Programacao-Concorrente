import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BoundedBuffer {
    private int[] buf;
    private int iget = 0;
    private int iput = 0;
    private int nelems = 0;

    Lock l = new ReentrantLock();
    Condition notEmpty = l.newCondition();
    Condition notFull = l.newCondition();

    public BoundedBuffer(int N) {
        buf = new int[N];

    }

    public int get() throws InterruptedException {
        l.lock();
        try {
            while (nelems == 0)
                notEmpty.await();
            int res;
            res = buf[iget];
            iget = (iget + 1) % buf.length;
            notFull.signal();
            notifyAll();
            nelems -= 1;
            return res;
        } finally {
            l.unlock();
        }
    }

    public void put(int v) throws InterruptedException {
        l.lock();
        try {
            while (nelems == buf.length)
                notFull.await();
            buf[iput] = v;
            iput = (iput + 1) % buf.length;
            nelems += 1;
            notEmpty.signal();
        } finally {
            l.unlock();
        }
    }

}

class Warehouse {
    Lock l = new ReentrantLock();

    private class Item {
        int quant = 0;
        Condition cond = l.newCondition();
    }

    Map<String, Item> map = new HashMap<>();

    private Item get(String s) {
        Item item = map.get(s);
        if (item == null) {
            item = new Item();
            map.put(s, item);
        }
        return item;
    }

    public void supply(String s, int quant) {
        l.lock();
        try {
            Item item = get(s);
            item.quant += quant;
            item.cond.signal();
        } finally {
            l.unlock();
        }
    }

    public void consume(String[] items) throws InterruptedException {
        l.lock();
        try {
            for (String s : items) {
                Item item = get(s);
                while (item.quant == 0)
                    item.cond.await();
                item.quant -= 1;
            }
        } finally {
            l.unlock();
        }
    }
}

/*
 * class Teste{
 * public static void main(String[] args){
 * 
 * BoundedBuffer b = new BoundedBuffer(20);
 * 
 * new Thread(() -> {
 * try{
 * for(int i=1;;i++){
 * System.out.println("Vou fazer put de: " + i);
 * b.put(i);
 * System.out.println("Fiz put de: " + i);
 * Thread.sleep(1000);
 * }
 * }
 * catch(InterruptedException e){}
 * 
 * }).start();
 * 
 * new Thread(() -> {
 * try{
 * for(int i=1;;i++){
 * System.out.println("Vou fazer get");
 * int x = b.get();
 * System.out.println("Fiz get de: " + x);
 * Thread.sleep(2000);
 * }
 * }
 * catch(InterruptedException e){}
 * 
 * }).start();
 * 
 * 
 * }
 * }
 */