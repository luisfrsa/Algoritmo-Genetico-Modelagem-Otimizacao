package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

//    static List<Vertice> listaVertices = new ArrayList<>();
    static TreeMap<Integer, List<Vertice>> listaVertices = new TreeMap<>();
//    static List<Solucao> listaSolucoes = new ArrayList<>();
    static TreeMap<Double, Solucao> listaSolucoes = new TreeMap<>();
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

    static public void main(String[] args) throws IOException {
        Leitura leitura = new Leitura();
        new Solucao(leitura.readFile());
        qdePecas = leitura.qdePecas;
        qdeMedianas = leitura.qdeMedianas;

        int size_solucoes = listaSolucoes.size();
        while (size_solucoes < qdePopulacao) {
            Solucao solucao = new Solucao();
            solucao.iniciaPopulacaoAleatoria(qdeMedianas, qdePecas);
            solucao.calculaCusto();
            listaSolucoes.put(solucao.custo, solucao);
            size_solucoes++;
        }
        for (Map.Entry<Double, Solucao> entry : listaSolucoes.entrySet()) {
            Double custo = entry.getKey();
            Solucao solucao = entry.getValue();
//            System.out.println(custo);
//            break;
        }
        Solucao solucao1;
        Solucao solucao2;
        Solucao nova_solucao;
        int iteracoes = 0;
        while (listaSolucoes.firstEntry().getValue().custo > 17000) {
            solucao1 = Genetico.torneio(listaSolucoes, 3);
            solucao2 = Genetico.torneio(listaSolucoes, 3);
            nova_solucao = Genetico.cruzar(solucao1, solucao2);
//            System.out.println("Size solucao " + nova_solucao.medianas.size());
            nova_solucao = Genetico.mutacao(nova_solucao, 4, 3);
            nova_solucao.calculaCusto();/*colocar calculacusto return false caso impossivel, e while =false*/
            if (nova_solucao.custo < listaSolucoes.lastEntry().getKey() && !listaSolucoes.containsKey(nova_solucao.custo)) {
                listaSolucoes.remove(listaSolucoes.lastEntry().getKey());
                listaSolucoes.put(nova_solucao.custo, nova_solucao);
            }
            System.out.println(iteracoes + " Tamanho->" + listaSolucoes.size() + " - Melhor-> " + listaSolucoes.firstEntry().getKey() + " Pior-> " + listaSolucoes.lastEntry().getKey());
            iteracoes++;
        }
        System.out.println(listaSolucoes.firstEntry().getValue().medianas);
        System.out.println(listaSolucoes.lastEntry().getKey());
        System.out.println(listaSolucoes.firstEntry().getKey());
    }
}

class Genetico {

    static Solucao cruzar(Solucao solucao1, Solucao solucao2) {
        ArrayList<Mediana> medianas_cruzadas = cruzaMedianas(solucao1, solucao2);
        Solucao retorno = new Solucao();
        retorno.medianas = medianas_cruzadas;
        return retorno;
    }

    static Solucao mutacao(Solucao solucao, int taxa_mucacao, int qde_bits) {
        int random;
        random = (int) Math.floor(Math.random() * 101);
        if (random < taxa_mucacao) {
            List<Mediana> novas_medianas = new ArrayList<>();
            Mediana novaMediana;
            Vertice randomVertice;
            Integer randomIndex = (int) (Math.random() * (solucao.listaVertices.size() - 1));
            while (qde_bits > 0) {
//                randomVertice = solucao.listaVertices.get((int) (Math.random() * (solucao.listaVertices.size() - 1)));
                randomVertice = CustomTreeMap.getRandomVertice(solucao.listaVertices, randomIndex);
                if (!solucao.containsV(randomVertice)) {
//                    estou aqui, basta remover vertice da treemap
                    random = (int) (Math.random() * solucao.medianas.size());//index da mediana que sera substituida
                    solucao.medianas.remove(random);
                    novaMediana = new Mediana();
                    novaMediana.vertice_mediana = randomVertice;
//                    solucao.listaVertices = CustomTreeMap.removeTreemap(solucao.listaVertices,randomIndex,randomVertice);
                    solucao.medianas.add(novaMediana);
                    qde_bits--;
                }
            }
        }
        return solucao;
    }

    static ArrayList<Mediana> cruzaMedianas(Solucao solucao1, Solucao solucao2) {
        int tamanho_medianas_solucao = solucao1.medianas.size();
        int random;
        ArrayList<ArrayList<Mediana>> intersessaoDisjuncao = solucao1.intersessaoDesjuncao(solucao2);
        ArrayList<Mediana> novas_medianas = intersessaoDisjuncao.get(0);
        ArrayList<Mediana> desjuncao_medianas = intersessaoDisjuncao.get(1);

        int tamanho_novas = novas_medianas.size();
        while (tamanho_novas < tamanho_medianas_solucao) {
            random = (int) (Math.random() * desjuncao_medianas.size());
            novas_medianas.add(desjuncao_medianas.get(random));
            desjuncao_medianas.remove(random);
            tamanho_novas++;
        }

//        System.out.println("Size cruzmaento " + novas_medianas.size());
        return novas_medianas;
    }

    /*verificar se para cruzar dois elementos, realizar 2x o algoritmo ou, 1x e utilizar os dois melhores*/
    static Solucao torneio(TreeMap<Double, Solucao> listaSolucoes, int num_elementos) {
        TreeMap<Double, Solucao> listaCompetidores = new TreeMap<>();
        List<Double> keys = new ArrayList<>(listaSolucoes.keySet());
        Random random = new Random();
        /*substituir por for (0,k) para ver qual é mais rápido*/
        while (listaCompetidores.size() < num_elementos) {
            Double randomKey = keys.get(random.nextInt(keys.size()));
            Solucao escolhido = listaSolucoes.get(randomKey);
            listaCompetidores.put(escolhido.custo, escolhido);
        }
        return listaCompetidores.firstEntry().getValue();
    }
}

class Solucao {

    static final AtomicInteger contador = new AtomicInteger(0);
    int id;
    static TreeMap<Integer, List<Vertice>> listaVertices = new TreeMap<>();
    List<Mediana> medianas = new ArrayList<>();
    double custo;

    public Solucao() {
        id = contador.incrementAndGet();
    }

    public Solucao(TreeMap<Integer, List<Vertice>> listaVertices) {
        Solucao.listaVertices = listaVertices;
    }

    /*verificar quantidade de loops para preencher*/
    void iniciaPopulacaoAleatoria(int qdeMedianas, int qdePecas) {
        int random;
        int maxRandom = (int) Math.floor(qdePecas / qdeMedianas);
        int size_mediana = medianas.size();
        while (size_mediana < qdeMedianas) {
//            for (Vertice v : listaVertices) {
            for (Map.Entry<Integer, List<Vertice>> entry : listaVertices.entrySet()) {
//                Vertice v = entry.getValue();
                for (Vertice v : entry.getValue()) {
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
    }

    void calculaCusto() {
        custo = 0;
        int countVertice = 0;
        for (Map.Entry<Integer, List<Vertice>> entry : listaVertices.entrySet()) {
            for (Vertice v : entry.getValue()) {
                custo += v.calculaDistanciaVertices(v.getMedianaProximaLivre(medianas).vertice_mediana);
                countVertice++;
            }
        }

        System.out.println(" numero de vertices1 " + listaVertices.size());
        System.out.println(" numero de vertices2 " + countVertice);
    }

    ArrayList<ArrayList<Mediana>> intersessaoDesjuncao(Solucao other) {
        ArrayList<ArrayList<Mediana>> retorno = new ArrayList<>();
        ArrayList<Mediana> intersessao = new ArrayList<>();
        ArrayList<Mediana> desjuncao = new ArrayList<>();
        ArrayList<Mediana> thisM = new ArrayList<>(this.medianas.size());
        ArrayList<Mediana> otherM = new ArrayList<>(other.medianas.size());

        for (Mediana m1 : this.medianas) {
            thisM.add(new Mediana(m1));
        }
        for (Mediana m2 : other.medianas) {
            otherM.add(new Mediana(m2));
        }
//        for (Mediana m1 : thisM) {
//            for (Mediana m2 : otherM) {
//                if (m1.vertice_mediana.id == m2.vertice_mediana.id) {
//                    System.out.println("Entrou");
//                    intersessao.add(m1);
//                }
//            }
//        }
        for (int i = 0; i >= thisM.size() - 1; i++) {
            for (int j = 0; j >= otherM.size() - 1; j++) {
//                System.out.println(thisM.get(i).vertice_mediana.id + " - " + otherM.get(j).vertice_mediana.id);
                if (thisM.get(i).vertice_mediana.id == otherM.get(j).vertice_mediana.id) {
//                    System.out.println("Entrou!");
                    intersessao.add(thisM.get(i));
                    thisM.remove(thisM.get(i));
                    otherM.remove(otherM.get(j));
                }
            }
        }

        for (Mediana m1 : thisM) {
//            System.out.println("addedendo1-> " + m1.vertice_mediana.id);
//            System.out.println("size 1 -> " + desjuncao.size());
            desjuncao.add(m1);
        }
        for (Mediana m2 : otherM) {
//            System.out.println("addedendo2-> " + m2.vertice_mediana.id);
//            System.out.println("size 2 -> " + desjuncao.size());
            desjuncao.add(m2);
        }
        retorno.add(intersessao);
        retorno.add(desjuncao);
        return retorno;
    }

    boolean containsV(Vertice v) {
        if (v == null) {
            return true;
        }
        for (Mediana m : this.medianas) {
            if (m.vertice_mediana.id == v.id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String ls = System.getProperty("line.separator");
        String retorno;
        String strCusto = String.valueOf((int) custo);
        retorno = "Solução-> ID[" + id + "], Custo[" + strCusto + "] " + ls;
//        for (Mediana m : medianas) {
//            retorno += m.toString();
//        }
        return retorno;
    }

}

class Mediana {

    static final AtomicInteger contador = new AtomicInteger(0);
    int id;
    int demanda_atual;
    int qde_vertices = 0;
    Vertice vertice_mediana;
    List<Vertice> lista_vertices = new ArrayList<>();

    public Mediana() {
        id = contador.incrementAndGet();
    }

    /*para CLONE*/
    public Mediana(Mediana m) {
        this.id = m.id;
        this.vertice_mediana = m.vertice_mediana;
        this.demanda_atual = 0;
    }

    @Override
    public String toString() {
        String ls = System.getProperty("line.separator");
        String retorno;
//        retorno = "[" + id + "][" + vertice_mediana.id + "] Mediana: CAP[" + vertice_mediana.capacidade + "], DEM[" + demanda_atual + "]" + ls + vertice_mediana.toString() + ls;
        retorno = "Mediana -> ID_MED [" + vertice_mediana.id + "], ID_VERTICE [" + id + "], CAPACIDADE[" + vertice_mediana.capacidade + "], DEMANDA [" + demanda_atual + "], QDE VERT " + qde_vertices + ls;
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

//    /*para CLONE*/
//    public Vertice(Vertice v) {
//        this.id = v.id;
//        this.posX = v.posX;
//        this.posY = v.posY;
//        this.capacidade = v.capacidade;
//        this.demanda = v.demanda;
//    }
    Mediana getMedianaProximaLivre(List<Mediana> medianas) {
        int capacidade_mediana, soma_cap_demanda;
        TreeMap<Double, List<Mediana>> listaDistancias = new TreeMap<>();
        int sizeGG = 0;
        for (Mediana mediana : medianas) {
            sizeGG++;
            listaDistancias = CustomTreeMap.addTreemap(listaDistancias, this.calculaDistanciaVertices(mediana.vertice_mediana), mediana);
        }

        for (Map.Entry<Double, List<Mediana>> entry : listaDistancias.entrySet()) {
            for (Mediana mediana : entry.getValue()) {
                capacidade_mediana = mediana.vertice_mediana.capacidade;
                soma_cap_demanda = (mediana.demanda_atual + this.demanda);
                if (capacidade_mediana >= soma_cap_demanda) {
                    mediana.demanda_atual = soma_cap_demanda;
                    mediana.lista_vertices.add(this);
                    mediana.qde_vertices++;
                    return mediana;
                }
            }
        }
        System.out.println("Sem medianas com capacidades");
        System.out.println("size treemap " + listaDistancias.size());
        System.out.println("size gg " + listaDistancias.size());
        for (Map.Entry<Double, List<Mediana>> entry : listaDistancias.entrySet()) {
            for (Mediana mediana : entry.getValue()) {
                System.out.println(mediana);
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
        String retorno = "Vertice-> ID [" + id + "], Demanda [" + demanda + "]" + ls;
        return retorno;
    }
}

class CustomTreeMap {

    static Vertice getRandomVertice(TreeMap<Integer, List<Vertice>> treeMap, Integer randomIndex) {
        List<Vertice> randomVertice;
        randomVertice = treeMap.get(randomIndex);
        int randint = (int) Math.floor(Math.random() * (randomVertice.size()-1));
        Vertice v = null;
        v = randomVertice.get(randint);
        null pointer
//        System.out.println("Rand index = " + randint);
//        System.out.println("On size = " + randomVertice.size());
//        System.out.println(v);
        return v;
    }

    static TreeMap<Integer, List<Vertice>> removeTreemap(TreeMap<Integer, List<Vertice>> treeMap, Integer valor, Vertice obj) {
        List<Vertice> tempList = treeMap.get(valor);
        tempList.remove(obj);
        treeMap.put(valor, tempList);
        return treeMap;
    }

    static TreeMap<Integer, List<Vertice>> addTreemap(TreeMap<Integer, List<Vertice>> treeMap, Integer valor, Vertice obj) {
        List<Vertice> tempList = null;
        if (treeMap.containsKey(valor)) {
            tempList = treeMap.get(valor);
            if (tempList == null) {
                tempList = new ArrayList();
            }
            tempList.add(obj);
        } else {
            tempList = new ArrayList();
            tempList.add(obj);
        }
        treeMap.put(valor, tempList);
        return treeMap;
    }

    static TreeMap<Double, List<Mediana>> addTreemap(TreeMap<Double, List<Mediana>> treeMap, Double valor, Mediana obj) {
        List<Mediana> tempList = null;
        if (treeMap.containsKey(valor)) {
            tempList = treeMap.get(valor);
            if (tempList == null) {
                tempList = new ArrayList();
            }
            tempList.add(obj);
        } else {
            tempList = new ArrayList();
            tempList.add(obj);
        }
        treeMap.put(valor, tempList);
        return treeMap;
    }

//    static TreeMap<Double, List<Mediana>> addTreemap(TreeMap<Double, List<Mediana>> listaDistancias, Double calculaDistanciaVertices, Mediana mediana) {
//        return addTreemap(listaDistancias, calculaDistanciaVertices, mediana);
//    }
}

class Leitura {

    int qdePecas = 0;
    int qdeMedianas = 0;

    TreeMap<Integer, List<Vertice>> readFile() throws IOException {
        return readFile("src/main/caso1.txt");
    }

    TreeMap<Integer, List<Vertice>> readFile(String arquivo) throws IOException {
        TreeMap<Integer, List<Vertice>> listaV = new TreeMap(Collections.reverseOrder());
        String content = new String(Files.readAllBytes(Paths.get(arquivo)));
        String lines[] = content.split("[\\r\\n]+");
        int ind = 0;
        int soma_demanda = 0;
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
                                soma_demanda += v.demanda;
//                                listaV = CustomTreeMap.addTreemap(listaV, v.demanda, v);
                                listaV = CustomTreeMap.addTreemap(listaV, v.demanda, v);
                                break;
                            default:
                                break;
                        }
                    }
                    ind++;
                }
            }
        }
//        Main.exit(String.valueOf(soma_demanda));
        return listaV;

//                for (Map.Entry<Double, Mediana> entry : listaDistancias.entrySet()) {
//                }
//        System.out.println(m2.vertice_mediana);
//        Vertice v1 = new Vertice();
//        double dist = v1.calculaDistanciaVertices(m1.vertice_mediana, m2.vertice_mediana);
    }
}
