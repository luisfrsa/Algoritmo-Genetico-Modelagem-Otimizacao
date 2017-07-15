package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {

    static List<Vertice> listaVertices = new ArrayList<>();
    static List<Solucao> listaSolucoes = new ArrayList<>();
    static int qdePecas = 0;
    static int qdeMedianas = 0;

    public static void main(String[] args) throws IOException {
        System.out.println("teste");
        readFile();
    }

    static void readFile() throws IOException {
        readFile("src/main/caso1.txt");
    }

    static void calcula_objetivo() {
    }

    static void readFile(String arquivo) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(arquivo)));
        String lines[] = content.split("[\\r\\n]+");
        int ind = 0;
        Vertice v = new Vertice();
        Solucao solucao = new Solucao();
        for (String linha : lines) {
            String splits[] = linha.split(" ");
            for (String elem : splits) {
                if (elem.length() > 0) {
                    if (ind == 0) {
                        qdePecas = Integer.parseInt(elem);
                    } else if (ind == 1) {
                        qdeMedianas = Integer.parseInt(elem);
                    } else {
                        switch ((ind - 2) % 4) {
                            case 0:
                                v = new Vertice();
                                v.posX = Integer.parseInt(elem);
                                break;
                            case 1:
                                v.posY = Integer.parseInt(elem);
                                break;
                            case 2:
                                v.capacidade = Integer.parseInt(elem);
                                break;
                            case 3:
                                v.demanda = Integer.parseInt(elem);
                                listaVertices.add(v);
                                Mediana m = new Mediana();
                                m.vertice_mediana = v;
                                solucao.medianas.add(m);
                                break;
                            default:
                                break;
                        }
//                      System.out.println(elem + " " + String.valueOf(ind) + " ");
                    }
                    ind++;
                }
            }
        }
//                for (Map.Entry<Double, Mediana> entry : listaDistancias.entrySet()) {
//                }
        Mediana m1 = null;
        Mediana m2 = null;
        for (Mediana m : solucao.medianas) {
            if (m1 != null) {
                m2 = m;
                System.out.println("b");
                break;
            } else {
                System.out.println("a");
                m1 = m;
            }

        }
        System.out.println(m1.vertice_mediana);
        System.out.println(m2.vertice_mediana);
        Vertice v1 = new Vertice();
        double dist = v1.calculaDistanciaVertices(m1.vertice_mediana, m2.vertice_mediana);
//        double dist = v1.calculaPitagoras(0,4,3,0);
        System.out.println(dist);
        /*for (Vertice ver : listaVertices) {
            System.out.println(ver.toString());
        }*/
    }
}

class Solucao {

    List<Mediana> medianas = new ArrayList<>();
    double custo;
}

class Mediana {

    Vertice vertice_mediana;
    List<Vertice> lista_vertices = new ArrayList<>();

}

class Vertice {

    int posX;
    int posY;
    int capacidade;
    int demanda;
//calcular valores

    Mediana getMedianaProximaLivre(List<Mediana> medianas) {
        HashMap<Double, Mediana> listaDistancias = new HashMap<>();
        for (Mediana mediana : medianas) {
            listaDistancias.put(this.calculaDistanciaVertices(this, mediana.vertice_mediana), mediana);
        }
        for (Map.Entry<Double, Mediana> entry : listaDistancias.entrySet()) {
            Double key = entry.getKey();
            Mediana value = entry.getValue();
            System.out.println(key);
            System.out.println(value.lista_vertices.toString());
        }

        return new Mediana();
    }

    Double calculaDistanciaVertices(Vertice vertice1, Vertice vertice2) {
        return this.calculaPitagoras(vertice1.posX, vertice2.posX, vertice1.posY, vertice2.posY);
    }

    Double calculaPitagoras(int x1, int x2, int y1, int y2) {
        Double retorno = Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
        return retorno;
//        return  Math.round(retorno * 100);
    }

    @Override
    public String toString() {
        String retorno = "posX -> " + posX;
        retorno += " posY -> " + posY;
        retorno += " capacidade -> " + capacidade;
        retorno += " demanda -> " + demanda;
        return retorno;
    }
}
