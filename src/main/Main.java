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

class Main {

    static TreeMap<Integer, List<Vertice>> listaVertices = new TreeMap<>();
    static ArrayList<Vertice> arrayListaVertices = new ArrayList<>();
    static TreeMap<Double, Solucao> listaSolucoes = new TreeMap<>();
    static int run_codes = 0;
    static int quantidade_mutacao = 0;
    static int qdePecas = 0;
    static int qdeMedianas = 0;

    static int qdePopulacao = 1000;
    static int taxaMutacao = 10;
    static int bitsMutacao = 0;
    static int bitsMutacaoRatio = 3;  // 10 medianas /3 = 3 -> bitsMutacao = Numero de medianas/ratio */
    static int qdeSorteio = (int) Main.qdePopulacao / 20;  // 100 vertices /20 = 5 -> bitsMutacao = Numero de medianas/ratio */
    static int pontoParada = 500;
    static int tipoCruzamento = 1; //0->aleatorio, 1->intersessao*/
    static int tipoMutacao = 1; // 0->aleatorio, 1->bits proximos*/

    static void debug() {
        System.out.println(".::Debug::.");
    }

    static void exit(String s) {
        System.out.println(s);
        System.exit(0);
    }

    static public void main(String[] args) throws IOException {
        long time_init = System.currentTimeMillis();
        Leitura leitura = new Leitura();
        Relatorio relatorio = new Relatorio();
        Solucao s = new Solucao(leitura.readFile("caso1.txt"));
        if (Main.run_codes == 0) {
            System.out.println("Tempo de leitura da entrada: " + ((System.currentTimeMillis() - time_init) / 1000) + "s ");
            time_init = System.currentTimeMillis();
            Genetico.calculaDistanciasVertices();
            System.out.println("Calculando distancia vertices: " + ((System.currentTimeMillis() - time_init) / 1000) + "s ");
            time_init = System.currentTimeMillis();
        }
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

        if (Main.run_codes == 0) {
            System.out.println("Tempo para gerar populacao inicial aleatoria: " + ((System.currentTimeMillis() - time_init) / 1000) + "s ");
            time_init = System.currentTimeMillis();
        }

        Solucao solucao1;
        Solucao solucao2;
        Solucao nova_solucao;

        while (countParada <= pontoParada && listaSolucoes.firstEntry().getValue().custo > 0) {
            solucao1 = Genetico.torneio(listaSolucoes, qdeSorteio);
            solucao2 = Genetico.torneio(listaSolucoes, qdeSorteio);
            nova_solucao = Genetico.cruzar(solucao1, solucao2, tipoCruzamento);
            nova_solucao = Genetico.mutacao(nova_solucao, taxaMutacao, bitsMutacao);
            nova_solucao.calculaCusto();

            if (nova_solucao.custo < listaSolucoes.lastEntry().getKey() && !listaSolucoes.containsKey(nova_solucao.custo)) {
                if (nova_solucao.custo < listaSolucoes.firstEntry().getKey() && Main.run_codes == 0) {
                    System.out.println(iteracoes + " Tamanho-> " + listaSolucoes.size() + " - Melhor-> " + listaSolucoes.firstEntry().getKey() + " Pior-> " + listaSolucoes.lastEntry().getKey());
                    countParada = 0;
                }
                listaSolucoes.remove(listaSolucoes.lastEntry().getKey());
                listaSolucoes.put(nova_solucao.custo, nova_solucao);
                if (Main.run_codes == 0) {
                    relatorio.add(iteracoes, listaSolucoes.firstEntry().getKey());
                }
            }
            countParada++;
            iteracoes++;
        }
        if (Main.run_codes == 0) {
            System.out.println("Tempo para encontrar melhor solucao local: " + ((System.currentTimeMillis() - time_init) / 1000) + "s ");
            System.out.println("Memoria usada->" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (double) (1024 * 1024)));
            relatorio.geraRelatorio();
        }
//        System.out.println(listaSolucoes.firstEntry().getValue().medianas);
        if (Main.run_codes == 1) {
            System.out.println(listaSolucoes.lastEntry().getKey());
        }
//        System.out.println(listaSolucoes.firstEntry().getKey());
    }

    private static class Relatorio {

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

    private static class Genetico {

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

        static void calculaDistanciasVertices() {
            Vertice v1;
            Double distancia;
            List<Vertice> vertices1 = new ArrayList<>();
            for (Vertice v : arrayListaVertices) {
                vertices1.add(v);
            }
            for (Vertice v : arrayListaVertices) {
                for (int i = 0; i <= vertices1.size() - 1; i++) {
                    v1 = vertices1.get(i);
                    distancia = v.calculaDistanciaVertices(v1);
//                    if (distancia != 0) {
                    v.distanciaVertices = CustomTreeMap.addTreemap(v.distanciaVertices, distancia, v1);
                    v.distanciaVerticesHash.put(v1.hashCode(), distancia);
//                    }
                }
            }
//            for (Map.Entry<Integer, List<Vertice>> entry : listaVertices.entrySet()) {
//                for (Vertice v : entry.getValue()) {
//                    System.out.println(v.distanciaVerticesHash.size());
//                }
//            }

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

        static Solucao mutacao_aleatoria(Solucao solucao, int taxa_mucacao, int qde_bits) {
            int random;
            random = (int) Math.floor(Math.random() * 101);
            if (random < taxa_mucacao) {
                Main.quantidade_mutacao++;
                List<Mediana> novas_medianas = new ArrayList<>();
                Mediana novaMediana;
                Vertice randomVertice;
                Integer randomIndex;
                while (qde_bits > 0) {
                    randomVertice = Solucao.arrayListaVertices.get((int) Math.floor(Math.random() * (Solucao.arrayListaVertices.size())));
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

        static Solucao mutacao_proxima(Solucao solucao, int taxa_mucacao, int qde_bits) {
            int random;
            random = (int) Math.floor(Math.random() * 101);
            if (random < taxa_mucacao) {
                Main.quantidade_mutacao++;
                List<Mediana> novas_medianas = new ArrayList<>();
                Mediana novaMediana;
                Vertice randomVertice;
                Integer maxRand;
//                System.out.println(" init mut " + solucao.medianas);
                while (qde_bits > 0) {
                    random = (int) (Math.random() * solucao.medianas.size());//index da mediana que sera substituida
                    Mediana m = solucao.medianas.get(random);
                    List<Double> keys = new ArrayList<>(m.vertice_mediana.distanciaVertices.keySet());
                    maxRand = (int) Math.floor((keys.size() / 30));

                    Random randomgg = new Random();
                    Double randomKey = keys.get(randomgg.nextInt(maxRand));
                    List<Vertice> l = m.vertice_mediana.distanciaVertices.get(randomKey);
                    randomVertice = l.get((int) Math.floor(Math.random() * (l.size())));

                    if (!solucao.containsV(randomVertice)) {
                        random = (int) (Math.random() * solucao.medianas.size());//index da mediana que sera substituida
                        solucao.medianas.remove(random);
                        novaMediana = new Mediana();
                        novaMediana.vertice_mediana = randomVertice;
                        solucao.medianas.add(novaMediana);
                        qde_bits--;
                    }
                }
//                System.out.println(solucao.medianas);
            }
            return solucao;
        }

        static Solucao mutacao(Solucao solucao, int taxa_mucacao, int qde_bits) {
            if (Main.tipoMutacao == 0) {
                return mutacao_aleatoria(solucao, taxa_mucacao, qde_bits);
            } else {
                return mutacao_proxima(solucao, taxa_mucacao, qde_bits);

            }

        }

        /*verificar se para cruzar dois elementos, realizar 2x o algoritmo ou, 1x e utilizar os dois melhores*/
        static Solucao torneio(TreeMap<Double, Solucao> listaSolucoes, int num_elementos) {
            TreeMap<Double, Solucao> listaCompetidores = new TreeMap<>();
            List<Double> keys = new ArrayList<>(listaSolucoes.keySet());
            Random random = new Random();
            /*substituir por for (0,k) para ver qual e mais rapido*/
            while (listaCompetidores.size() < num_elementos) {
                Double randomKey = keys.get(random.nextInt(keys.size()));
                Solucao escolhido = listaSolucoes.get(randomKey);
                listaCompetidores.put(escolhido.custo, escolhido);
            }
            return listaCompetidores.firstEntry().getValue();
        }
    }

    private static class Solucao {

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

        void iniciaPopulacaoAleatoria3(int qdeMedianas, int qdePecas) {
            int random;
            int maxRandom = (int) Math.floor(qdePecas / qdeMedianas);
            int size_mediana = medianas.size();
            while (size_mediana < qdeMedianas) {
                for (Map.Entry<Integer, List<Vertice>> entry : listaVertices.entrySet()) {
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

        void iniciaPopulacaoAleatoria(int qdeMedianas, int qdePecas) {
            int maxRandom = (int) Math.floor(qdePecas / qdeMedianas);
            int size_mediana = medianas.size();
            List<Integer> keys = new ArrayList<Integer>(listaVertices.keySet());
            while (size_mediana < qdeMedianas) {
                Random random = new Random();
                Integer randomKey = keys.get(random.nextInt(keys.size()));
                List<Vertice> vertices = listaVertices.get(randomKey);
                Random random2 = new Random();
                Vertice v = vertices.get(random2.nextInt(vertices.size()));
                if (!this.containsV(v)) {
                    Mediana m = new Mediana();
                    m.vertice_mediana = v;
                    medianas.add(m);
                    size_mediana++;
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
            retorno = "Solucao-> ID[" + id + "], Custo[" + strCusto + "] " + ls;
//        for (Mediana m : medianas) {
//            retorno += m.toString();
//        }
            return retorno;
        }

    }

    private static class Mediana {

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

    private static class Vertice {

        static final AtomicInteger contador = new AtomicInteger(0);
        TreeMap<Double, List<Vertice>> distanciaVertices = new TreeMap<>();
        HashMap<Integer, Double> distanciaVerticesHash = new HashMap<>();

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
            int size = 0;
            Double distancia;
            for (Mediana mediana : medianas) {
                size++;
                distancia = this.calculaDistanciaVertices(mediana.vertice_mediana);
//                 distancia = this.distanciaVerticesHash.get(mediana.vertice_mediana.hashCode());

                listaDistancias = CustomTreeMap.addTreemap(listaDistancias, distancia, mediana);
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
            System.out.println("size " + listaDistancias.size());
            for (Map.Entry<Double, List<Mediana>> entry : listaDistancias.entrySet()) {
                for (Mediana mediana : entry.getValue()) {
                    System.out.println(mediana);
                }
            }
            Main.exit(" Erro! Nao foi encontrada mediana com espaco suficiente para ligar ao vertice");
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

    private static class CustomTreeMap {

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

        static TreeMap<Double, List<Vertice>> addTreemap(TreeMap<Double, List<Vertice>> treeMap, Double valor, Vertice obj) {
            List<Vertice> tempList = null;
            if (treeMap.containsKey(valor)) {
                tempList = treeMap.get(valor);
                if (tempList == null) {
                    tempList = new ArrayList<>();
                }
                tempList.add(obj);
            } else {
                tempList = new ArrayList<>();
                tempList.add(obj);
            }
            treeMap.put(valor, tempList);
            return treeMap;
        }

        static TreeMap<Integer, List<Vertice>> addTreemap(TreeMap<Integer, List<Vertice>> treeMap, Integer valor, Vertice obj) {
            List<Vertice> tempList = null;
            if (treeMap.containsKey(valor)) {
                tempList = treeMap.get(valor);
                if (tempList == null) {
                    tempList = new ArrayList<>();
                }
                tempList.add(obj);
            } else {
                tempList = new ArrayList<>();
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
                    tempList = new ArrayList<>();
                }
                tempList.add(obj);
            } else {
                tempList = new ArrayList<>();
                tempList.add(obj);
            }
            treeMap.put(valor, tempList);
            return treeMap;
        }

//    static TreeMap<Double, List<Mediana>> addTreemap(TreeMap<Double, List<Mediana>> listaDistancias, Double calculaDistanciaVertices, Mediana mediana) {
//        return addTreemap(listaDistancias, calculaDistanciaVertices, mediana);
//    }
    }

    private static class Leitura {

        int qdePecas = 0;
        int qdeMedianas = 0;

        TreeMap<Integer, List<Vertice>> readFile() throws IOException {
            return readFile("caso1.txt");
//        return readFile("caso2.txt");
//        return readFile("caso3.txt");
//        return readFile("caso4.txt");
//        return readFile("caso5.txt");
//        return readFile("caso6.txt");
//            return readFile("caso7.txt");
//        return readFile("caso8.txt");
//        return readFile("caso9.txt");
        }

        TreeMap<Integer, List<Vertice>> readFile(String arquivo) throws IOException {
            TreeMap<Integer, List<Vertice>> listaV = new TreeMap<>(Collections.reverseOrder());
            ArrayList<Vertice> arrayV = new ArrayList<>();
            int ind = 0;
            int soma_demanda = 0;
            Vertice v = new Vertice();
            Scanner scan;
            if (Main.run_codes == 1) {
                scan = new Scanner(System.in);
            } else {
                scan = new Scanner(new FileReader(Main.class.getResource(arquivo).getPath()));
            }
            qdePecas = scan.nextInt();
            qdeMedianas = scan.nextInt();
            Main.bitsMutacao = (int) Math.floor(qdeMedianas / Main.bitsMutacaoRatio);
            if (Main.bitsMutacao < 2) {
                Main.bitsMutacao = 2;
            }
            scan.nextLine();
//	vertices = new Vertice[nrVertices];
//        for (int i = 0; i < qdePecas; i++) {
            while (scan.hasNext()) {
                switch (ind % 4) {
                    case 0:
                        v = new Vertice();
                        v.posX = (int) Double.parseDouble(scan.next());
                        break;
                    case 1:
                        v.posY = (int) Double.parseDouble(scan.next());
                        break;
                    case 2:
                        v.capacidade = (int) Double.parseDouble(scan.next());
                        break;
                    case 3:
                        v.demanda = (int) Double.parseDouble(scan.next());
                        soma_demanda += v.demanda;
//                                listaV = CustomTreeMap.addTreemap(listaV, v.demanda, v);
                        listaV = CustomTreeMap.addTreemap(listaV, v.demanda, v);
                        arrayV.add(v);
                        break;
                    default:
                        break;
                }
                ind++;
            }
            scan.close();
            Main.listaVertices = listaV;
            Main.arrayListaVertices = arrayV;
            return listaV;
        }

        TreeMap<Integer, List<Vertice>> readFilegg(String arquivo) throws IOException {
            TreeMap<Integer, List<Vertice>> listaV = new TreeMap<>(Collections.reverseOrder());
            ArrayList<Vertice> arrayV = new ArrayList<>();
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
//                                v.posX = (int) Double.parseDouble(elem);
                                    v.posX = (int) Double.parseDouble(elem);
                                    break;
                                case 1:
                                    v.posY = (int) Double.parseDouble(elem);
                                    break;
                                case 2:
                                    v.capacidade = (int) Double.parseDouble(elem);
                                    break;
                                case 3:
                                    v.demanda = (int) Double.parseDouble(elem);
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
}
