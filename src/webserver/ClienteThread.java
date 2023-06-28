package webserver;

import classes.Assentos;
import classes.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static classes.Teatro.assentos;

public class ClienteThread extends Thread {

    private Socket clienteSocket;
    private static int assento;
    private static final String LOGS_PATH = "logs";


    public ClienteThread(Socket clienteSocket) {
        this.clienteSocket = clienteSocket;
    }

    @Override
    public void run() {
        try{
            OutputStream logs = new FileOutputStream(LOGS_PATH + File.separator + "log.txt", true);
            InputStream in = clienteSocket.getInputStream();
            OutputStream out = clienteSocket.getOutputStream();

            Log log = new Log();

            byte[] buffer = new byte[1024];
            int nBytes = in.read(buffer);
            String str = new String(buffer, 0, nBytes);
            String[] linhas = str.split("\n");


            int i = 1;
            for (String linha : linhas) {
                System.out.println("[LINHA " + i + "] " + linha);
                i++;
            }

            String[] linha1 = linhas[0].split(" ");
            String recurso = linha1[1];
            System.out.println("[RECURSO] " + recurso);

            if (recurso.equals("/")) {
                recurso = "/index.html";
            }

            else if (recurso.contains("/reservarAssento.html")) {
                String[] arr = recurso.split("/?");
                this.assento = 0;

                //foreach para fazer um split da url tirando apenas o codigo
                for (String idTemp : arr) {
                    if(idTemp.equals("=")){
                        String[] idAssento = recurso.split("=");
                        this.assento = Integer.parseInt(idAssento[1]);
                    }
                }
                recurso = "/reservarAssento.html";
            }

            else if (recurso.contains("/esperaResposta.html")) { // pagina html de espera
                String[] arr = recurso.split("/?");
                String nome = "";
                int id = 0;

                // primeiro split para separar o nome e o id
                for (String variTemp: arr) {
                    if(variTemp.equals("=")){
                        String[] variarr = recurso.split("=");

                        nome = variarr[1];
                        id = Integer.parseInt(variarr[2]);
                    }
                }

                // segundo split para limpar o nome pois sujo com "&id"
                String[] splitnome = nome.split("&");
                nome = splitnome[0];

                boolean disp = testaAssento(id);

                if(disp == false){
                    for(Assentos a : assentos){
                        if(a.getIdAssento() == id){ //preenche os dados no objeto assento
                            a.setOcupado(true);
                            a.setNome(nome);
                            a.setData(new Date());
                            InetAddress endIP = InetAddress.getLocalHost();
                            a.setIp(endIP.getHostAddress());
                        }
                    }
                    String logMessage = log.geraLog(id); // função para preencher uma string com os dados do assento

                    FileWriter fileWriter = new FileWriter(LOGS_PATH + File.separator + "log.txt", true);
                    fileWriter.write(logMessage);
                    fileWriter.write(System.lineSeparator()); // Adiciona uma quebra de linha após cada registro
                    fileWriter.close();
                }
                recurso = "/index.html";
            }

            recurso = recurso.replace('/', File.separatorChar);
            String header = "HTTP/1.1 200 OK\n" +
                    "Content-Type: " + getContentType(recurso) + "; charset=utf-8\n\n";
            File f = new File("arquivos_html" + recurso);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            if (!f.exists()) {
                bout.write("404 NOT FOUND\n\n".getBytes(StandardCharsets.UTF_8));
            } else {
                InputStream fileIn = new FileInputStream(f);
                bout.write(header.getBytes(StandardCharsets.UTF_8));
                //escreve arquivo
                nBytes = fileIn.read(buffer);
                do {
                    if (nBytes > 0) {
                        bout.write(buffer, 0, nBytes);
                        nBytes = fileIn.read(buffer);
                    }
                } while (nBytes == 1024);
            }


            String saida = processaVariaveis(bout, this.assento);
            out.write(saida.getBytes(StandardCharsets.UTF_8));

            out.flush();
            out.close();
            logs.close();
            clienteSocket.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }


    private static String processaVariaveis(ByteArrayOutputStream bout, int assento) { // aqui tem as funções para preencher o html
        String str = new String(bout.toByteArray());
        str = str.replace("%%%", reescreveLugares());
        str = str.replace("***", reescreveForm(assento));

        return str;
    }


    private static String reescreveLugares(){ // retorna uma string com o html separa em div de assento ocupado e livre
        String lugares = "";

        int cont = 0;
        for(Assentos assento : assentos){
            if(cont == 0){
                lugares += "<div class='fileira'>";
            }
            if(cont == 5){
                lugares += "<div class='fileira'>";
            }

            if(assento.isOcupado() == true){
                lugares += "<div class='assento1'> <h2>Assento " + assento.getIdAssento() + "</h2> <p>Nome: " + assento.getNome() + "</p> <p>Data: " + assento.getData() + "</p> <button id='" + assento.getIdAssento() + "' name='idAssento' value='" + assento.getIdAssento() + "' class='ocupado' disabled >Reservar</button> </div>";
            }else{
                lugares += "<div class='assento2'> <h2>Assento " + assento.getIdAssento() + "</h2> <button id='" + assento.getIdAssento() + "' name='idAssento' value='" + assento.getIdAssento() + "' class='livre' >Reservar</button> </div>";
            }

            if(cont == 4){
                lugares += "</div>";
            }
            if(cont == 10){
                lugares += "</div>";
            }

            cont++;
        }

        return lugares;
    }

    private static String reescreveForm(int assento){ // retorna uma string com o formulario
        Integer.toString(assento);

        String form =   "<label for='nome'>Insira seu nome completo: </label> " +
                "<input type='text' name='nome' id='nome'> " +
                "<input type='hidden' value='" + assento + "' name='id' id='id'> " +
                "<div class='bot'> <button type='submit'>Reservar</button> </div>";

        return form;
    }

    private static boolean testaAssento(int id){ // função para testar se o assento esta livre ou ocupado

        for(Assentos a:assentos){
            if(a.getIdAssento() == id){
                if(a.isOcupado() == true){
                    return true;
                }
                else{
                    return false;
                }
            }
        }

        return false;
    }

    private static String getContentType(String nomeRecurso) {
        if (nomeRecurso.toLowerCase().endsWith(".css")) {
            return "text/css";
        } else if (nomeRecurso.toLowerCase().endsWith(".jpg")
                || nomeRecurso.toLowerCase().endsWith(".jpeg"))
        {
            return "image/jpeg";
        } else if (nomeRecurso.toLowerCase().endsWith(".png"))
        {
            return "image/png";
        } else if (nomeRecurso.toLowerCase().endsWith(".js"))
        {
            return "application/javascript";
        } else {
            return "text/html";
        }
    }

}
