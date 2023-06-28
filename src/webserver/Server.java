package webserver;

import classes.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;



public class Server extends Thread{

    private FilaRequisicoes filaRequisicoes;
    private static Semaphore semaphore;

    public static void main(String[] args) throws IOException {
        //recebe conexão
        ServerSocket ss = new ServerSocket(88);
        OutputStream logs = new FileOutputStream("log.txt");
        System.out.println("Iniciando servidor...");

        Teatro t = new Teatro();
        t.criaAssentos(); // cria o objeto teatro e instancia todos o assentos em uma array

        Server server = new Server();
        server.start();

        semaphore = new Semaphore(10);

        while(true){
            Socket clienteSocket = ss.accept();
            System.out.println("Cliente conectado: "+ clienteSocket.getInetAddress().getHostAddress());

            server.adicionarRequisicao(clienteSocket);
        }
    }

    public Server() {
        filaRequisicoes = new FilaRequisicoes();
    }

    public synchronized void adicionarRequisicao(Socket socket) {
        filaRequisicoes.adicionarRequisicao(socket);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clienteSocket = filaRequisicoes.obterRequisicao();
                semaphore.acquire(); // Adquire a permissão do semáforo
                // Crie uma nova instância de ClienteThread e inicie-a em uma nova thread
                ClienteThread clienteThread = new ClienteThread(clienteSocket);
                clienteThread.start();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                semaphore.release();
            }
        }
    }
}