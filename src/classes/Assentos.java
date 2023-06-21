package classes;

import java.util.Date;

public class Assentos {
    private int idAssento;
    private String nome;
    private String ip;
    private Date data;
    private boolean ocupado = false;

    public int getIdAssento() {
        return idAssento;
    }
    public void setIdAssento(int idAssento) {
        this.idAssento = idAssento;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getData() {
        return data;
    }
    public void setData(Date data) {
        this.data = data;
    }

    public boolean isOcupado() {
        return ocupado;
    }
    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }
}
