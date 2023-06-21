package classes;

import java.util.ArrayList;

public class Teatro {
    public static ArrayList<Assentos> assentos = new ArrayList<>();
    public static int numAssentos = 10;

    public void criaAssentos(){
        for(int i = 1; i <= this.numAssentos; i++){ // função para instanciar os objetos
            Assentos assento = new Assentos();
            assento.setIdAssento(i);

            this.assentos.add(assento);
        }
    }
}
