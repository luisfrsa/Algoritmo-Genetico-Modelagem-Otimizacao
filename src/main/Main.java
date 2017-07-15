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
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    static List<Vertice> listaVertices = new ArrayList<>();
    static List<Solucao> listaSolucoes = new ArrayList<>();
    static int qdePecas = 0;
    static int qdeMedianas = 0;
    static int qdePopulacao = 10;

    static void debug() {
        System.out.println(".::Debug::.");
    }

    static void exit(String s) {
        System.out.println(s);
        System.exit(0);
    }

    public static void main(String[] args) throws IOException {

        Leitura leitura = new Leitura();
        new Solucao(leitura.readFile());
        qdePecas = leitura.qdePecas;
        qdeMedianas = leitura.qdeMedianas;

        int size_solucoes = listaSolucoes.size();
        while (size_solucoes < qdePopulacao) {
            Solucao solucao = new Solucao();
            solucao.iniciaPopulacaoAleatoria(qdeMedianas, qdePecas);
            listaSolucoes.add(solucao);
            size_solucoes++;
        }
        for (Solucao s : listaSolucoes) {
            s.calculaCusto();
            System.out.println(s.custo);
        }
    }

}

class Solucao {

    static final AtomicInteger contador = new AtomicInteger(0);
    int id;
    static List<Vertice> listaVertices = new ArrayList<>();
    List<Mediana> medianas = new ArrayList<>();
    double custo;

    public Solucao() {
        id = contador.incrementAndGet();
    }

    public Solucao(List<Vertice> listaVertices) {
        Solucao.listaVertices = listaVertices;
    }

    void iniciaPopulacaoAleatoria(int qdeMedianas, int qdePecas) {
        int random;
        int maxRandom = (int) Math.floor(qdePecas / qdeMedianas);
        int size_mediana = medianas.size();
        while (size_mediana < qdeMedianas) {
            for (Vertice v : listaVertices) {
                if (size_mediana < qdeMedianas) {
                    random = (int) (Math.random() * maxRandom);
                    if (random == 1) {
                        Mediana m = new Mediana();
                        m.vertice_mediana = v;
                        medianas.add(m);
                        size_mediana++;
                    }
                } else {
                    break;
                }
            }
        }
    }

    void calculaCusto() {
        for (Vertice v : listaVertices) {
            custo += v.calculaDistanciaVertices(v.getMedianaProximaLivre(medianas).vertice_mediana);
            break;
        }
//        System.out.println(c);
//        System.out.println("fim custo");
    }

    @Override
    public String toString() {
        String ls = System.getProperty("line.separator");
        String retorno;
        retorno = "[" + id + "] Solução: " + ls;
        for (Mediana m : medianas) {
            retorno += m.toString();
        }
        return retorno;
    }

}

class Mediana {

    static final AtomicInteger contador = new AtomicInteger(0);
    int id;
    int demanda_atual;
    Vertice vertice_mediana;
    List<Vertice> lista_vertices = new ArrayList<>();

    public Mediana() {
        id = contador.incrementAndGet();
    }

    @Override
    public String toString() {
        String ls = System.getProperty("line.separator");
        String retorno;
        retorno = "[" + id + "][" + vertice_mediana.id + "] Mediana: CAP[" + vertice_mediana.capacidade + "], DEM[" + demanda_atual + "]" + ls + vertice_mediana.toString() + ls;
        for (Vertice v : lista_vertices) {
            retorno += v.toString();
        }
        return retorno;
    }
}

class Vertice {

    static final AtomicInteger contador = new AtomicInteger(0);
    int id;
    int posX;
    int posY;
    int capacidade;
    int demanda;

    public Vertice() {
        id = contador.incrementAndGet();
    }

    Mediana getMedianaProximaLivre(List<Mediana> medianas) {
        int capacidade_mediana, soma_cap_demanda;
//        HashMap<Double, Mediana> listaDistancias = new HashMap<>();
        TreeMap<Double, Mediana> listaDistancias = new TreeMap<>();
        for (Mediana mediana : medianas) {
            listaDistancias.put(this.calculaDistanciaVertices(mediana.vertice_mediana), mediana);
        }
        for (Map.Entry<Double, Mediana> entry : listaDistancias.entrySet()) {
//            Double key = entry.getKey();
//            System.out.println(" key-> " + key);
//            Mediana value = entry.getValue();
//            System.out.println(" vertice-> " + value);
            Mediana mediana = entry.getValue();
            capacidade_mediana = mediana.vertice_mediana.capacidade;
            soma_cap_demanda = (mediana.demanda_atual + this.demanda);
//            System.out.println(capacidade_mediana);
//            System.out.println(soma_cap_demanda);
            if (capacidade_mediana >= soma_cap_demanda) {
                mediana.demanda_atual = soma_cap_demanda;
                return mediana;
            }
        }
        Main.exit(" Erro! Não foi encontrada mediana com espaço suficiente para ligar ao vértice");
        return new Mediana();
    }

    Double calculaDistanciaVertices(Vertice vertice) {
        return this.calculaPitagoras(this.posX, vertice.posX, this.posY, vertice.posY);
    }

    Double calculaPitagoras(int x1, int x2, int y1, int y2) {
        Double retorno = Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
        return retorno;
//        return  Math.round(retorno * 100);
    }

    @Override
    public String toString() {
        String ls = System.getProperty("line.separator");
        String retorno = "[" + id + "] Vertice ";
        retorno += " posX -> " + posX;
        retorno += " posY -> " + posY;
        retorno += " capacidade -> " + capacidade;
        retorno += " demanda -> " + demanda;
        return retorno;
    }
}

class Leitura {

    int qdePecas = 0;
    int qdeMedianas = 0;

    List<Vertice> readFile() throws IOException {
        return readFile("src/main/caso1.txt");
    }

//    static void calcula_objetivo() {
//    }
    List<Vertice> readFile(String arquivo) throws IOException {
        List<Vertice> listaV = new ArrayList<>();
        String content = new String(Files.readAllBytes(Paths.get(arquivo)));
        String lines[] = content.split("[\\r\\n]+");
        int ind = 0;
        Vertice v = new Vertice();
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
                                listaV.add(v);
                                break;
                            default:
                                break;
                        }
                    }
                    ind++;
                }
            }
        }
        return listaV;

//                for (Map.Entry<Double, Mediana> entry : listaDistancias.entrySet()) {
//                }
//        System.out.println(m2.vertice_mediana);
//        Vertice v1 = new Vertice();
//        double dist = v1.calculaDistanciaVertices(m1.vertice_mediana, m2.vertice_mediana);
    }
}
