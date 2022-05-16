class MyThread extends Thread {
    int I;

    MyThread(int x) {
        this.I = x;
    }

    public void run() {
        for (int i = 0; i < I; i++) {
            System.out.println(i + 1);
        }
    }
}

class Main {
    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        MyThread[] threads = new MyThread[N];

        for (int i = 0; i < N; i++) {
            threads[i] = new MyThread(i + 1);
            threads[i].start();
        }

        try {
            for (int i = 0; i < N; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
        }
    }
}