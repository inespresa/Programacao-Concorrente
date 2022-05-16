import java.util.Random;

interface Jogo {
    Partida participa() throws InterruptedException;
}

interface Partida {
    String adivinha(int n);
}

class JogoImpl implements Jogo {
    PartidaImpl p = new PartidaImpl();
    int jogadores = 0;

    public synchronized Partida participa() throws InterruptedException {
        PartidaImpl ps = p;
        jogadores += 1;
        if (jogadores == 4) {
            notifyAll();
            jogadores = 0;
            p = new PartidaImpl();
        } else {
            while (p == ps)
                wait();
        }
        return ps;
    }
}

class PartidaImpl implements Partida {
    int tentativas = 0;
    boolean timeout = false;
    boolean ganhou = false;
    int numero = new Random().nextInt();

    void start() {
        new Thread(() -> {
            Thread.sleep(60000);
            timeout();
        }).start();
    }

    synchronized void timeout() {
        timeout = true;
    }

    public synchronized String adivinha(int n) {
        if (ganhou)
            return "PERDEU";
        if (timeout)
            return "TEMPO";
        if (tentativas >= 100)
            return "TENTATIVAS";
        if (n == numero) {
            ganhou = true;
            return "GANHOU";
        }
        tentativas += 1;
        return numero > n ? "MAIOR" : "MENOR";
    }
}