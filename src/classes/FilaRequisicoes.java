package classes;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FilaRequisicoes {
    private List<Socket> fila;

    public FilaRequisicoes() {
        fila = new ArrayList<>();
    }

    public synchronized void adicionarRequisicao(Socket socket) {
        fila.add(socket);
        notify(); // Notifica as threads consumidoras de que uma nova requisição foi adicionada
    }

    public synchronized Socket obterRequisicao() throws InterruptedException {
        while (fila.isEmpty()) {
            wait(); // Aguarda até que haja uma requisição na fila
        }
        return fila.remove(0);
    }
}
