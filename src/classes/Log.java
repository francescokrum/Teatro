package classes;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static classes.Teatro.assentos;

public class Log {
    String logMessage;
    public String geraLog(int id){ // função para pegar os dados do assento enviar para uma string e retorna-la
        for(Assentos a : assentos){
            if(a.getIdAssento() == id){
                String disponibilidade = "";

                if(a.isOcupado() == true){
                    disponibilidade = "ocupado";
                }else{
                    disponibilidade = "vago";
                }

                logMessage =    "-----------------x-----------------\n" +
                                "Nome: " + a.getNome() + "\n" +
                                "IP: " + a.getIp() + "\n" +
                                "Data: " + a.getData() + "\n" +
                                "Assento: " + id + "\n" +
                                "Disponibilidade: " + disponibilidade + "\n\n";
            }
        }

        return logMessage;
    }
}
