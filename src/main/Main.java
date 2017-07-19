package main;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    static TreeMap<Integer, List<Vertice>> listaVertices = new TreeMap<>();
    static ArrayList<Vertice> arrayListaVertices = new ArrayList<>();
    static TreeMap<Double, Solucao> listaSolucoes = new TreeMap<>();
    static int run_codes = 0;
    static int quantidade_mutacao = 0;
    static int qdePecas = 0;
    static int qdeMedianas = 0;

    static int qdePopulacao = 100;
    static int taxaMutacao = 10;
    static int bitsMutacao = 3;
    static int qdeSorteio = 3;
    static int pontoParada = 500;
    static int tipoCruzamento = 1;/*0->aleatorio, 1->intersessao*/
    static void debug() {
        System.out.println(".::Debug::.");
    }

    static void exit(String s) {
        System.out.println(s);
        System.exit(0);
    }

    static public void main(String[] args) throws IOException {
        Leitura leitura = new Leitura();
        Relatorio relatorio = new Relatorio();
        Solucao s = new Solucao(leitura.readFile());
        qdePecas = leitura.qdePecas;
        qdeMedianas = leitura.qdeMedianas;
        int iteracoes = 0;
        int countParada = 0;

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
        }
        Solucao solucao1;
        Solucao solucao2;
        Solucao nova_solucao;
        while (countParada <= pontoParada && listaSolucoes.firstEntry().getValue().custo > 0) {
            solucao1 = Genetico.torneio(listaSolucoes, qdeSorteio);
            solucao2 = Genetico.torneio(listaSolucoes, qdeSorteio);
            nova_solucao = Genetico.cruzar(solucao1, solucao2, tipoCruzamento);
            nova_solucao = Genetico.mutacao(nova_solucao, taxaMutacao, bitsMutacao);
            nova_solucao.calculaCusto();/*colocar calculacusto return false caso impossivel, e while =false*/
            if (nova_solucao.custo < listaSolucoes.lastEntry().getKey() && !listaSolucoes.containsKey(nova_solucao.custo)) {
                listaSolucoes.remove(listaSolucoes.lastEntry().getKey());
                listaSolucoes.put(nova_solucao.custo, nova_solucao);
                System.out.println(iteracoes + " Tamanho->" + listaSolucoes.size() + " - Melhor-> " + listaSolucoes.firstEntry().getKey() + " Pior-> " + listaSolucoes.lastEntry().getKey());
                countParada = 0;
                if (run_codes == 0) {
                    relatorio.add(iteracoes, listaSolucoes.firstEntry().getKey());
                }
            }
            countParada++;
            iteracoes++;
        }
        if (run_codes == 0) {
            relatorio.geraRelatorio();
        }
//        System.out.println(listaSolucoes.firstEntry().getValue().medianas);
//        System.out.println(listaSolucoes.lastEntry().getKey());
//        System.out.println(listaSolucoes.firstEntry().getKey());
    }
}

class Relatorio {

    TreeMap<Integer, Double> iteracoes = new TreeMap<>();

    void add(Integer iter, Double melhor) {
        iteracoes.put(iter, melhor);
    }

    void geraRelatorio() throws IOException {
        System.out.println("Iniciando escrita relatorio");
        String strprint = "var geneticData = [";
        for (Map.Entry<Integer, Double> entry : iteracoes.entrySet()) {
            strprint += "[" + entry.getKey() + "," + entry.getValue() + "],";
        }
        strprint += "];";
        escreveRelatorioJs(strprint);
        System.out.println("Fim escrita relatorio");
    }

    void escreveRelatorioJs(String data) throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("grafico/data.js"), "utf-8"))) {
            writer.write(data);
        }
    }
}

class Genetico {

    static Solucao cruzar(Solucao solucao1, Solucao solucao2, int tipoCruzamento) {
        ArrayList<Mediana> medianas_cruzadas = null;
        switch (tipoCruzamento) {
            case 1:
                medianas_cruzadas = cruzaMedianasIntersessao(solucao1, solucao2);
                break;
            case 0:
            default:
                medianas_cruzadas = cruzaMedianasBitsAleatorios(solucao1, solucao2);
                break;
        }
        Solucao retorno = new Solucao();
        retorno.medianas = medianas_cruzadas;
        return retorno;
    }

    static ArrayList<Mediana> cruzaMedianasBitsAleatorios(Solucao solucao1, Solucao solucao2) {
        int tamanho_medianas_solucao = solucao1.medianas.size();
        int random;
        ArrayList<ArrayList<Mediana>> intersessaoDisjuncao = solucao1.intersessaoDesjuncao(solucao2);
        ArrayList<Mediana> novas_medianas = new ArrayList<>();
        ArrayList<Mediana> merge_medianas = new ArrayList<>(solucao1.medianas);
        merge_medianas.removeAll(solucao2.medianas);
        merge_medianas.addAll(solucao2.medianas);
        Mediana medianaAdd;
        int tamanho_novas = 0;
        while (tamanho_novas < tamanho_medianas_solucao) {
            random = (int) (Math.random() * merge_medianas.size());
            medianaAdd = new Mediana(merge_medianas.get(random));
            novas_medianas.add(medianaAdd);
            merge_medianas.remove(random);
            tamanho_novas++;
        }
        return novas_medianas;
    }

    static ArrayList<Mediana> cruzaMedianasIntersessao(Solucao solucao1, Solucao solucao2) {
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
        return novas_medianas;
    }

    static Solucao mutacao(Solucao solucao, int taxa_mucacao, int qde_bits) {
        int random;
        random = (int) Math.floor(Math.random() * 101);
        if (random < taxa_mucacao) {
            Main.quantidade_mutacao++;
//            System.out.println("Mutacao " + Main.quantidade_mutacao);
            List<Mediana> novas_medianas = new ArrayList<>();
            Mediana novaMediana;
            Vertice randomVertice;
            Integer randomIndex;
            while (qde_bits > 0) {
                randomVertice = solucao.arrayListaVertices.get((int) Math.floor(Math.random() * (solucao.arrayListaVertices.size())));
                if (!solucao.containsV(randomVertice)) {
                    random = (int) (Math.random() * solucao.medianas.size());//index da mediana que sera substituida
                    solucao.medianas.remove(random);
                    novaMediana = new Mediana();
                    novaMediana.vertice_mediana = randomVertice;
                    solucao.medianas.add(novaMediana);
                    qde_bits--;
                }
            }
//            System.out.println(solucao.medianas);
        }
        return solucao;
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
    static ArrayList<Vertice> arrayListaVertices = new ArrayList<>();

    List<Mediana> medianas = new ArrayList<>();
    double custo;

    public Solucao() {
        id = contador.incrementAndGet();
    }

    public Solucao(TreeMap<Integer, List<Vertice>> listaVertices) {
        Solucao.listaVertices = listaVertices;
        arrayListaVertices = Main.arrayListaVertices;

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
//        System.out.println(" numero de vertices1 " + listaVertices.size());
//        System.out.println(" numero de vertices2 " + countVertice);
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
//        System.out.println("ContainsV" + v);
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
//        PRINT MEDIANA!!!
//        for (Vertice v : lista_vertices) {
//            retorno += v.toString();
//        }
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

    public Vertice(int posX, int posY, int capacidade, int demanda) {
        this.posX = posX;
        this.posY = posY;
        this.capacidade = capacidade;
        this.demanda = demanda;
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

//    static Vertice getRandomVertice(TreeMap<Integer, List<Vertice>> treeMap, Integer randomIndex) {
//        List<Vertice> randomVertice;
//        System.out.println("Tree size= " + treeMap.size());
//        System.out.println("Index Param= " + randomIndex);
//        randomVertice = treeMap.get(randomIndex);
//        System.out.println("treemap with param = " + randomVertice);
//        int randint = (int) Math.floor(Math.random() * (randomVertice.size()));
//        System.out.println("Rand index = " + randint);
//        System.out.println("On size = " + randomVertice.size());
//        Vertice v = null;
//        v = randomVertice.get(randint);
//        System.out.println(v);
//        return v;
//    }
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
//        return readFile("src/main/caso1.txt");
//        return readFile("src/main/caso2.txt");
        return readFile("src/main/caso3.txt");
    }

//    TreeMap<Integer, List<Vertice>> readFile3(String arquivo) throws IOException {
//        TreeMap<Integer, List<Vertice>> listaV = new TreeMap(Collections.reverseOrder());
//        ArrayList<Vertice> arrayV = new ArrayList<>();
////        Scanner scan = new Scanner(new FileReader(Main.class.getResource(arquivo).getPath()));
//        Scanner scan = new Scanner(new FileReader(Main.class.getResource(arquivo).getPath()));
//        qdePecas = scan.nextInt();
//        qdeMedianas = scan.nextShort();
//        for (int i = 0; i < qdePecas; i++) {
//            Vertice v = new Vertice(scan.nextInt(), scan.nextInt(), scan.nextShort(), scan.nextShort());
//            listaV = CustomTreeMap.addTreemap(listaV, v.demanda, v);
//            arrayV.add(v);
//        }
//        scan.close();
//        return listaV;
//    }

    TreeMap<Integer, List<Vertice>> readFile(String arquivo) throws IOException {
        TreeMap<Integer, List<Vertice>> listaV = new TreeMap(Collections.reverseOrder());
        ArrayList<Vertice> arrayV = new ArrayList<>();
        String content = new String(Files.readAllBytes(Paths.get(arquivo)));
        String lines[] = content.split("[\\r\\n]+");
        int ind = 0;
        int soma_demanda = 0;
        Vertice v = new Vertice();
        for (String linha : lines) {
            String splits[] = linha.split("\\t");
            for (String elem : splits) {
                if (elem.length() > 0) {
//                    System.out.println(elem);
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
                                arrayV.add(v);
                                break;
                            default:
                                break;
                        }
                    }
                    ind++;
                }
            }
        }
        Main.arrayListaVertices = arrayV;
//        System.out.println(arrayV);
        return listaV;

//                for (Map.Entry<Double, Mediana> entry : listaDistancias.entrySet()) {
//                }
//        System.out.println(m2.vertice_mediana);
//        Vertice v1 = new Vertice();
//        double dist = v1.calculaDistanciaVertices(m1.vertice_mediana, m2.vertice_mediana);
    }
}
