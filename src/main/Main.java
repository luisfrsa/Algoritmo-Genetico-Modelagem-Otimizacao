
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

class Main {

    static TreeMap<Integer, List<Vertice>> listaVertices = new TreeMap<>();
    static ArrayList<Vertice> arrayListaVertices = new ArrayList<>();
    static TreeMap<Double, Solucao> listaSolucoes = new TreeMap<>();
    static Random rand = new Random(System.currentTimeMillis());
    static String arquivo_lido;
    static int run_codes = 0;
    static int quantidade_mutacao = 0;
    static int qdePecas = 0;
    static int qdeMedianas = 0;
    static int bitsMutacao = 100;
    static int taxaMutacao = 5;

    static int qdePopulacao = 20;
    static int qdeSorteio = Main.qdePopulacao / 10;
    static int pontoParada = 50;
    static int tipoCruzamento = 0; //0->aleatorio, 1->intersessao*/
    static int tipoMutacao = 0; // 0->aleatorio, 1->bits proximos*/

    static void debug() {
        System.out.println(".::Debug::.");
    }

    static void exit() {
        Main.exit("exit");
    }

    static void exit(String s) {
        System.out.println(s);
        System.exit(0);
    }

    static public void main(String[] args) throws IOException {
        Scanner scan;
        scan = new Scanner(System.in);
        arquivo_lido = scan.nextLine();
        for (int i = 0; i <= 10; i++) {
            executaAlgoritmo("caso" + arquivo_lido + ".txt", 200, 150);
        }
        if (true) {
            return;
        }
        for (int i = 0; i <= 10; i++) {
            arquivo_lido = "caso1.txt";
            executaAlgoritmo(arquivo_lido, 500, 100);
        }
        for (int i = 0; i <= 10; i++) {
            arquivo_lido = "caso2.txt";
            executaAlgoritmo(arquivo_lido, 500, 100);
        }

        for (int i = 0; i <= 10; i++) {

            arquivo_lido = "caso3.txt";
            executaAlgoritmo(arquivo_lido, 500, 100);
        }
        for (int i = 0; i <= 10; i++) {

            arquivo_lido = "caso4.txt";
            executaAlgoritmo(arquivo_lido, 500, 100);
        }
        for (int i = 0; i <= 10; i++) {

            arquivo_lido = "caso5.txt";
            executaAlgoritmo(arquivo_lido, 500, 100);
        }
        for (int i = 0; i <= 10; i++) {
            arquivo_lido = "caso6.txt";
            executaAlgoritmo(arquivo_lido, 10, 50);
        }
        for (int i = 0; i <= 10; i++) {
            arquivo_lido = "caso7.txt";
            executaAlgoritmo(arquivo_lido, 10, 50);
        }
        for (int i = 0; i <= 10; i++) {
            arquivo_lido = "caso8.txt";
            executaAlgoritmo(arquivo_lido, 10, 50);
        }
        for (int i = 0; i <= 10; i++) {
            arquivo_lido = "caso9.txt";
            executaAlgoritmo(arquivo_lido, 10, 50);
        }
    }

    static public void zeraStatic() {
        listaVertices = new TreeMap<>();
        arrayListaVertices = new ArrayList<>();
        listaSolucoes = new TreeMap<>();
        rand = new Random(System.currentTimeMillis());
    }

    static public void setParam(int pop, int parada) {
        Main.qdePopulacao = pop;
        Main.qdeSorteio = (int) Math.floor(Main.qdePopulacao / 10);
        if (Main.qdeSorteio < 2) {
            Main.qdeSorteio = 2;
        }
        Main.pontoParada = parada;
    }

    static public void executaAlgoritmo(String arquivo_lido, int pop, int parada) throws IOException {
        zeraStatic();
        setParam(pop, parada);
        long time_init = System.currentTimeMillis();
        String nome_arquivo_log = String.valueOf(time_init);
        Leitura leitura = new Leitura();
        Relatorio relatorio = new Relatorio();

        Solucao s = new Solucao(leitura.readFile(arquivo_lido, Main.run_codes));

        if (Main.run_codes == 0) {
            relatorio.escreve_log(nome_arquivo_log, "Arquivo de Log log_" + nome_arquivo_log + ".txt");
            relatorio.escreve_log(nome_arquivo_log, "Teste " + arquivo_lido);
            relatorio.escreve_log(nome_arquivo_log, "Parametros: ");
            relatorio.escreve_log(nome_arquivo_log, "SEM BUSCA NA VIZINHANCA");
            relatorio.escreve_log(nome_arquivo_log, "Tamanho da populacao " + qdePopulacao);
            relatorio.escreve_log(nome_arquivo_log, "Quantidade de solucoes para sorteio " + qdeSorteio);
            relatorio.escreve_log(nome_arquivo_log, "Tipo de cruzamento " + tipoCruzamento);
            relatorio.escreve_log(nome_arquivo_log, "Taxa de mutacao " + taxaMutacao);
            relatorio.escreve_log(nome_arquivo_log, "Quantidade de bits de mutacao " + bitsMutacao);
            relatorio.escreve_log(nome_arquivo_log, "Quantidade de iteracoes s/ melhoria para parada " + pontoParada);
            relatorio.escreve_log(nome_arquivo_log, "Tempo de leitura da entrada: " + ((System.currentTimeMillis() - time_init) / 1000) + "s ");
            time_init = System.currentTimeMillis();
        }
//        Genetico.calculaDistanciasVertices();
        if (Main.run_codes == 0) {
            relatorio.escreve_log(nome_arquivo_log, "Calculando distancia vertices: " + ((System.currentTimeMillis() - time_init) / 1000) + "s ");
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
//            solucao.verificaMedianasRepetidas();
            listaSolucoes.put(solucao.custo, solucao);
            size_solucoes++;
        }
        if (Main.run_codes == 0) {
            relatorio.escreve_log(nome_arquivo_log, "Tempo para gerar populacao inicial aleatoria: " + ((System.currentTimeMillis() - time_init) / 1000) + "s ");
            time_init = System.currentTimeMillis();
        }

        Solucao solucao1;
        Solucao solucao2;
        Solucao nova_solucao;

        while (countParada <= pontoParada && listaSolucoes.firstEntry().getValue().custo > 0) {
            solucao1 = Genetico.torneio(listaSolucoes, qdeSorteio);
            solucao2 = Genetico.torneio(listaSolucoes, qdeSorteio);
            if (solucao1.custo == solucao2.custo) {
//                System.out.println("solucoes iguais");
                continue;
            }
            nova_solucao = Genetico.cruzar(solucao1, solucao2, tipoCruzamento);
//            nova_solucao.verificaMedianasRepetidas();
            nova_solucao = Genetico.mutacao(nova_solucao, taxaMutacao, bitsMutacao);

//            nova_solucao = Genetico.buscaVizinhanca(nova_solucao);
            if (nova_solucao.custo < listaSolucoes.lastEntry().getKey() && !listaSolucoes.containsKey(nova_solucao.custo)) {
                if (nova_solucao.custo < listaSolucoes.firstEntry().getKey() && Main.run_codes == 0) {
                    relatorio.escreve_log(nome_arquivo_log, iteracoes + " Tamanho-> " + listaSolucoes.size() + " - Melhor-> " + listaSolucoes.firstEntry().getKey() + " Pior-> " + listaSolucoes.lastEntry().getKey());
                }
                countParada = 0;
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
            relatorio.escreve_log(nome_arquivo_log, "Tempo para encontrar melhor solucao local: " + ((System.currentTimeMillis() - time_init) / 1000) + "s ");
            relatorio.escreve_log(nome_arquivo_log, "Memoria usada->" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (double) (1024 * 1024)));
            relatorio.escreve_log(nome_arquivo_log, ".::Melhor solucao::. " + listaSolucoes.firstEntry().getKey());
            relatorio.geraRelatorio(nome_arquivo_log, arquivo_lido.replace(".txt", ""));
        }
        if (Main.run_codes == 1) {
            System.out.println(listaSolucoes.lastEntry().getKey());

        }
    }

    private static class Relatorio {

        TreeMap<Integer, Double> iteracoes = new TreeMap<>();

        void add(Integer iter, Double melhor) {
            iteracoes.put(iter, melhor);
        }

        void escreve_log(String nome_arq, String str) throws IOException {
            File file = new File(System.getProperty("user.dir") + "/log/log_" + nome_arq + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(str + "\n");
            bw.close();
        }

        void geraRelatorio(String nome_arq, String caso) throws IOException {
//            System.out.println("Iniciando escrita relatorio");
            String strprint = "var geneticData_" + nome_arq + " = [[" + nome_arq + "],";
            for (Map.Entry<Integer, Double> entry : iteracoes.entrySet()) {
                strprint += "[" + entry.getKey() + "," + entry.getValue() + "],";
            }
            strprint += "];\n";
            strprint += "geneticData_" + caso + ".push(geneticData_" + nome_arq + ");\n\n";
            escreveRelatorioJs(strprint, caso);
//            System.out.println("Fim escrita relatorio");
        }

        void escreveRelatorioJs(String data, String caso) throws IOException {
//            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
//                    new FileOutputStream("grafico/data.js"), "utf-8"))) {
//                writer.write(data);
//            }
            File file = new File(System.getProperty("user.dir") + "/grafico/data_" + caso + ".js");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            bw.close();
        }
    }

    private static class Genetico {

        static Solucao buscaVizinhanca(Solucao solucao) {
            int tipo = 3;// maneiras 1 e 0 de gerar vizinhos
            int nivel = 3;//profundidade de vizinho
            int encontrou_vizinho;
            Solucao melhor_vizinho = solucao;
            Solucao vizinho = null;
            while (nivel > 1) {
                encontrou_vizinho = 0;
                for (int a = 1; a <= tipo; a++) {
                    vizinho = Genetico.getMelhorVizinho(melhor_vizinho, a, 5);
                    if (melhor_vizinho.custo > vizinho.custo) {
                        encontrou_vizinho = 1;
                        melhor_vizinho = vizinho;
//                        return melhor_vizinho;
                    }
                }
                if (encontrou_vizinho == 0) {
//                    return melhor_vizinho;
                }
                nivel--;
            }
            return melhor_vizinho;
        }

        static Solucao getMelhorVizinho(Solucao solucao, int tipo, int N) {
            int size = solucao.medianas.size();

            Solucao melhor_vizinho = solucao;
            Solucao vizinho;
            int j;
            for (int i = 0; i < size; i++) {
                vizinho = new Solucao();
                j = 0;
                for (Mediana m : solucao.medianas) {
                    if (i == j) {
                        if (tipo == 1) {
                            vizinho.medianas.add(Genetico.getMelhorVizinhoTipo1(solucao, m));
                        } else if (tipo == 2) {
                            vizinho.medianas.add(Genetico.getMelhorVizinhoTipo2(solucao, m, N));
                        } else {
                            vizinho.medianas.add(Genetico.getMelhorVizinhoTipo3(solucao, m));
                        }
                    } else {
                        vizinho.medianas.add(new Mediana(m));
                    }
                    j++;
                }
                vizinho.calculaCusto();
                if (melhor_vizinho.custo > vizinho.custo) {
                    melhor_vizinho = vizinho;
                    if (Integer.parseInt(arquivo_lido) >= 7) {//casos mais pesados
                        return vizinho;
                    }
                }
            }

            return melhor_vizinho;
        }

        /**
         * Dada uma solucao S, gerar N vizinhos onde e trocada uma mediana 1 vez
         * para cada vizinho, de maneira que esta mediana seja removida e subs
         * tituida por um de seus vértices ligados. Este vertice e substituido
         * aleatoriamente se tipo=0, Este vertice e substituido aleatoriamente
         * dentre os N mais próximos se tipo=1,
         */
        static Mediana getMelhorVizinhoTipo1(Solucao solucao, Mediana m) {
            int random;
            int count = 0;
            Mediana retorno;
            List<Vertice> lista_vertices = new ArrayList<>();
            for (Vertice v : m.lista_vertices) {
                lista_vertices.add(v);
            }
//            random = (int) (Math.random() * lista_vertices.size());
            random = rand.nextInt(lista_vertices.size());
            Vertice v = lista_vertices.get(random);
            while (solucao.containsV(v)) {
                if (count > 3 || lista_vertices.size() <= 1) {
                    retorno = new Mediana(m.vertice_mediana.id);
                    retorno.vertice_mediana = m.vertice_mediana;
                    return retorno;
                }
//                random = (int) (Math.random() * lista_vertices.size());
                random = rand.nextInt(lista_vertices.size());

                v = lista_vertices.get(random);
                lista_vertices.remove(random);
                count++;
            }
            retorno = new Mediana(v.id);
            retorno.vertice_mediana = v;

            return retorno;
        }

        static Mediana getMelhorVizinhoTipo2(Solucao solucao, Mediana m, int taxaQde) {
            int random;
            int count = taxaQde;
            TreeMap<Double, Vertice> listaDistancias = new TreeMap<>();
            List<Vertice> lista_vertices = new ArrayList<>();
            Mediana retorno;
            Double distancia;
            for (Vertice v : m.lista_vertices) {
                distancia = v.calculaDistanciaVertices(m.vertice_mediana);
                listaDistancias.put(distancia, v);
            }
            for (Map.Entry<Double, Vertice> entry : listaDistancias.entrySet()) {
                if (entry.getKey() > 0) {
                    lista_vertices.add(entry.getValue());
                    if (count == 0) {
                        break;
                    }
                    count--;
                }
            }
            if (lista_vertices.size() == 0) {
                retorno = new Mediana(m.vertice_mediana.id);
                retorno.vertice_mediana = m.vertice_mediana;
                return retorno;
            }
//            random = (int) (Math.random() * lista_vertices.size());
            random = rand.nextInt(lista_vertices.size());

            Vertice v = lista_vertices.get(random);
            while (solucao.containsV(v)) {
                if (count > 3 || lista_vertices.size() <= 1) {
                    retorno = new Mediana(m.vertice_mediana.id);
                    retorno.vertice_mediana = m.vertice_mediana;
                    return retorno;
                }
//                random = (int) (Math.random() * lista_vertices.size());
                random = rand.nextInt(lista_vertices.size());

                v = lista_vertices.get(random);
                lista_vertices.remove(random);
                count++;
            }
            retorno = new Mediana(v.id);
            retorno.vertice_mediana = v;
            return retorno;
        }

        static Mediana getMelhorVizinhoTipo3(Solucao solucao, Mediana m) {
            int random;
            int count = 0;
            Mediana retorno;
            random = rand.nextInt(arrayListaVertices.size());

            Vertice v = arrayListaVertices.get(random);
            while (solucao.containsV(v)) {
                if (count > 3) {
                    retorno = new Mediana(m.vertice_mediana.id);
                    retorno.vertice_mediana = m.vertice_mediana;
                    return retorno;
                }
//                random = (int) (Math.random() * lista_vertices.size());
                random = rand.nextInt(arrayListaVertices.size());

                v = arrayListaVertices.get(random);
                count++;
            }
            retorno = new Mediana(v.id);
            retorno.vertice_mediana = v;
            return retorno;
        }

        static Solucao cruzar(Solucao solucao1, Solucao solucao2, int tipoCruzamento) {
            ArrayList<Mediana> medianas_cruzadas = null;
            Solucao retorno = null;
            switch (tipoCruzamento) {
                case 1:
                    retorno = cruzaMedianasIntersessao(solucao1, solucao2);
                    break;
                case 0:
                default:
                    retorno = cruzaMedianasBitsAleatorios(solucao1, solucao2);
                    break;
            }
            return retorno;
        }

        static Solucao cruzaMedianasIntersessao(Solucao solucao1, Solucao solucao2) {
            int tamanho_medianas_solucao = solucao1.medianas.size();
            Solucao retorno = new Solucao();
            Solucao sol_temp = new Solucao();

            List<Mediana> lista_med1 = new ArrayList<>();
            List<Mediana> lista_med2 = new ArrayList<>();
            List<Mediana> lista_med_intersecao = new ArrayList<>();
            List<Mediana> lista_med_desjn = new ArrayList<>();
            Mediana m1;
            Mediana m2;
            for (int i = 0; i < solucao1.medianas.size(); i++) {
                sol_temp.medianas.add(new Mediana(solucao1.medianas.get(i)));
                sol_temp.medianas.add(new Mediana(solucao2.medianas.get(i)));
            }
            Collections.shuffle(sol_temp.medianas);
            for (int i = 0; i < sol_temp.medianas.size(); i++) {
                m1 = sol_temp.medianas.get(i);
                if (!retorno.containsV(m1.vertice_mediana)) {
                    retorno.medianas.add(m1);
                }
//                sol_temp.medianas.remove(i);
                if (retorno.medianas.size() >= solucao1.medianas.size()) {
                    break;
                }
            }
            if (true) {
                return retorno;
            }

            while (retorno.medianas.size() < solucao1.medianas.size()) {
            }
            for (int i = 0; i < lista_med1.size(); i++) {
                m1 = lista_med1.get(i);
                for (int j = 0; j < lista_med2.size(); j++) {
                    m2 = lista_med2.get(j);
                    if (lista_med_intersecao.contains(m2)) {
                        lista_med_intersecao.add(m1);
                    } else {
                        lista_med_desjn.add(m2);
                        lista_med2.remove(j);
                    }
                }
            }
            for (int j = 0; j < lista_med2.size(); j++) {
                m2 = lista_med2.get(j);
                lista_med_desjn.add(m2);
            }
            System.out.println("lista_med_intersecao  '' " + lista_med_intersecao.size());
            System.out.println("lista_med_desjn ''" + lista_med_desjn.size());
            Collections.shuffle(lista_med_desjn);
            for (int j = 0; j < lista_med_desjn.size(); j++) {
                lista_med_intersecao.add(lista_med_desjn.get(j));
                lista_med_desjn.remove(j);
                if (lista_med_intersecao.size() >= solucao1.medianas.size()) {
                    System.out.println("lista_med_intersecao " + lista_med_intersecao.size());
                    System.out.println("solucao1 " + solucao1.medianas.size());
                    System.out.println("Break");
                    break;
                }
            }
            System.out.println("lista_med_intersecaoggg " + lista_med_intersecao.size());
            System.out.println("solucaogggg " + solucao1.medianas.size());
            retorno.medianas = lista_med_intersecao;
            retorno.calculaCusto();
            return retorno;
        }

        static Solucao cruzaMedianasBitsAleatorios(Solucao solucao1, Solucao solucao2) {
            int tamanho_medianas_solucao = solucao1.medianas.size();
            Solucao sol_temp = new Solucao();
            Solucao retorno1 = new Solucao();
            Solucao retorno2 = new Solucao();
            Mediana m1;
            Mediana m2;
            for (int i = 0; i < solucao1.medianas.size(); i++) {
                m1 = solucao1.medianas.get(i);
                m2 = solucao2.medianas.get(i);
                if (!sol_temp.containsV(m1.vertice_mediana)) {
                    sol_temp.medianas.add(new Mediana(m1));
                }
                if (!sol_temp.containsV(m2.vertice_mediana)) {
                    sol_temp.medianas.add(new Mediana(m2));
                }
            }
            Collections.shuffle(sol_temp.medianas);
            int j = sol_temp.medianas.size() - 1;
            for (int i = 0; i < solucao1.medianas.size(); i++) {
                retorno1.medianas.add(sol_temp.medianas.get(i));
                retorno2.medianas.add(sol_temp.medianas.get(j));
                j--;
            }

            retorno1.calculaCusto();
            retorno2.calculaCusto();

            if (retorno1.custo < retorno2.custo) {
                return retorno1;
            }
            return retorno2;
        }

//        static ArrayList<Mediana> cruzaMedianasBitsAleatorios_old(Solucao solucao1, Solucao solucao2) {
//            int tamanho_medianas_solucao = solucao1.medianas.size();
//            int random;
//            ArrayList<ArrayList<Mediana>> intersessaoDisjuncao = solucao1.intersessaoDesjuncao(solucao2);
//            ArrayList<Mediana> novas_medianas = new ArrayList<>();
//            ArrayList<Mediana> merge_medianas = new ArrayList<>(solucao1.medianas);
//            merge_medianas.removeAll(solucao2.medianas);
//            merge_medianas.addAll(solucao2.medianas);
//            Mediana medianaAdd;
//            int tamanho_novas = 0;
//            while (tamanho_novas < tamanho_medianas_solucao) {
//                random = (int) (Math.random() * merge_medianas.size());
//                medianaAdd = new Mediana(merge_medianas.get(random));
//                novas_medianas.add(medianaAdd);
//                merge_medianas.remove(random);
//                tamanho_novas++;
//            }
//            return novas_medianas;
//        }
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

        static Solucao mutacao_aleatoria(Solucao solucao, int taxa_mucacao, int qde_bits) {
            int random;
//            random = (int) Math.floor(Math.random() * 101);
            random = rand.nextInt(101);

            if (random < taxa_mucacao) {
                Main.quantidade_mutacao++;
                List<Mediana> novas_medianas = new ArrayList<>();
                Mediana novaMediana;
                Vertice randomVertice;
                Integer randomIndex;
                while (qde_bits > 0) {
//                    randomVertice = Solucao.arrayListaVertices.get((int) Math.floor(Math.random() * (Solucao.arrayListaVertices.size())));
                    randomVertice = Solucao.arrayListaVertices.get(rand.nextInt(Solucao.arrayListaVertices.size()));
                    if (!solucao.containsV(randomVertice)) {
//                        random = (int) (Math.random() * solucao.medianas.size());//index da mediana que sera substituida
                        random = rand.nextInt(solucao.medianas.size());

                        solucao.medianas.remove(random);
                        novaMediana = new Mediana(randomVertice.id);
                        novaMediana.vertice_mediana = randomVertice;
                        solucao.medianas.add(novaMediana);
                        qde_bits--;
                    }
                }
//            System.out.println(solucao.medianas);
            }
            solucao.calculaCusto();
            return solucao;
        }

        static Solucao mutacao_proxima(Solucao solucao, int taxa_mucacao, int qde_bits) {
            /*NAO USADO POR CONSELHO DO PROFESSOR*/
            if (true) {
                return new Solucao();
            }
            int random;
//            random = (int) Math.floor(Math.random() * 101);
            random = rand.nextInt(101);

            if (random < taxa_mucacao) {
                Main.quantidade_mutacao++;
                List<Mediana> novas_medianas = new ArrayList<>();
                Mediana novaMediana;
                Vertice randomVertice;
                Integer maxRand;
//                System.out.println(" init mut " + solucao.medianas);
                while (qde_bits > 0) {
//                    random = (int) (Math.random() * solucao.medianas.size());//index da mediana que sera substituida
                    random = rand.nextInt(solucao.medianas.size());

                    Mediana m = solucao.medianas.get(random);
                    List<Double> keys = new ArrayList<>(m.vertice_mediana.distanciaVertices.keySet());
                    maxRand = (int) Math.floor((keys.size() / 30));
                    random = rand.nextInt(solucao.medianas.size());
//                    Random randomgg = new Random();
//                    Double randomKey = keys.get(randomgg.nextInt(maxRand));
                    Double randomKey = keys.get(rand.nextInt(maxRand));

                    List<Vertice> l = m.vertice_mediana.distanciaVertices.get(randomKey);
//                    randomVertice = l.get((int) Math.floor(Math.random() * (l.size())));
                    randomVertice = l.get(rand.nextInt(l.size()));

                    if (!solucao.containsV(randomVertice)) {
//                        random = (int) (Math.random() * solucao.medianas.size());//index da mediana que sera substituida
                        random = rand.nextInt(solucao.medianas.size());

                        solucao.medianas.remove(random);
                        novaMediana = new Mediana(randomVertice.id);
                        novaMediana.vertice_mediana = randomVertice;
                        solucao.medianas.add(novaMediana);
                        qde_bits--;
                    }
                }
//                System.out.println(solucao.medianas);
            }
            solucao.calculaCusto();
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
//            Random random = new Random();
            /*substituir por for (0,k) para ver qual e mais rapido*/
            while (listaCompetidores.size() < num_elementos) {
                Double randomKey = keys.get(rand.nextInt(keys.size()));
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
//                            random = (int) (Math.random() * maxRandom);
                            random = rand.nextInt(maxRandom);

                            if (random == 1) {
                                Mediana m = new Mediana(v.id);
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
//                Random random = new Random();
                Integer randomKey = keys.get(rand.nextInt(keys.size()));
                List<Vertice> vertices = listaVertices.get(randomKey);
//                Random random2 = new Random();
                Vertice v = vertices.get(rand.nextInt(vertices.size()));
                if (!this.containsV(v)) {
                    Mediana m = new Mediana(v.id);
                    m.vertice_mediana = v;
                    medianas.add(m);
                    size_mediana++;
                }
            }
        }

        void calculaCusto() {
            this.custo = 0;
            Mediana m;
            Double c;
            for (Mediana me : medianas) {
                me.demanda_atual = 0;
            }
            for (Map.Entry<Integer, List<Vertice>> entry : listaVertices.entrySet()) {
                for (Vertice v : entry.getValue()) {
                    m = v.getMedianaProximaLivre(medianas);
                    if (m != null) {
                        c = v.calculaDistanciaVertices(m.vertice_mediana);
                        this.custo += c;
                    } else {
                        this.custo = Double.MAX_VALUE;
                        break;
                    }
                }
            }
//            System.out.println(" custo " + this.custo);
//            System.out.println(" numero de vertices2 " + countVertice);
        }

//        ArrayList<ArrayList<Mediana>> intersessaoDesjuncao(Solucao other) {
//            ArrayList<ArrayList<Mediana>> retorno = new ArrayList<>();
//            ArrayList<Mediana> intersessao = new ArrayList<>();
//            ArrayList<Mediana> desjuncao = new ArrayList<>();
//            ArrayList<Mediana> thisM = new ArrayList<>(this.medianas.size());
//            ArrayList<Mediana> otherM = new ArrayList<>(other.medianas.size());
//
//            for (Mediana m1 : this.medianas) {
//                thisM.add(new Mediana(m1));
//            }
//            for (Mediana m2 : other.medianas) {
//                otherM.add(new Mediana(m2));
//            }
////        for (Mediana m1 : thisM) {
////            for (Mediana m2 : otherM) {
////                if (m1.vertice_mediana.id == m2.vertice_mediana.id) {
////                    System.out.println("Entrou");
////                    intersessao.add(m1);
////                }
////            }
////        }
//            for (int i = 0; i >= thisM.size() - 1; i++) {
//                for (int j = 0; j >= otherM.size() - 1; j++) {
////                System.out.println(thisM.get(i).vertice_mediana.id + " - " + otherM.get(j).vertice_mediana.id);
//                    if (thisM.get(i).vertice_mediana.id == otherM.get(j).vertice_mediana.id) {
////                    System.out.println("Entrou!");
//                        intersessao.add(thisM.get(i));
//                        thisM.remove(thisM.get(i));
//                        otherM.remove(otherM.get(j));
//                    }
//                }
//            }
//
//            for (Mediana m1 : thisM) {
////            System.out.println("addedendo1-> " + m1.vertice_mediana.id);
////            System.out.println("size 1 -> " + desjuncao.size());
//                desjuncao.add(m1);
//            }
//            for (Mediana m2 : otherM) {
////            System.out.println("addedendo2-> " + m2.vertice_mediana.id);
////            System.out.println("size 2 -> " + desjuncao.size());
//                desjuncao.add(m2);
//            }
//            retorno.add(intersessao);
//            retorno.add(desjuncao);
//            return retorno;
//        }
        boolean containsV(Vertice v) {
//        System.out.println("ContainsV" + v);
            if (v == null) {
                return true;
            }
            for (Mediana m : this.medianas) {
                if (m.id == v.id) {
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

        void verificaMedianasRepetidas() {
            int countMediana;
            for (Mediana m : this.medianas) {
                countMediana = 0;
                for (Mediana m2 : this.medianas) {
                    if (m.vertice_mediana.id == m2.vertice_mediana.id) {
                        countMediana++;
                        if (countMediana > 1) {
                            System.out.println("dumb -> " + m.vertice_mediana.id + " " + m2.vertice_mediana.id);
                            System.out.println(this.medianas);
                            Main.exit();

                            return;
                        }
                    }
                }
            }
        }
    }

    private static class Mediana {

        static final AtomicInteger contador = new AtomicInteger(0);
        int id;
        int demanda_atual;
        int qde_vertices = 0;
        Vertice vertice_mediana;
        List<Vertice> lista_vertices = new ArrayList<>();

        public Mediana(int i) {
//            id = contador.incrementAndGet();
            this.id = i;
        }

        /*para CLONE*/
        public Mediana(Mediana m) {
            this.id = m.id;
            this.vertice_mediana = m.vertice_mediana;
            this.demanda_atual = 0;
        }

        boolean containsV(Vertice v) {
            if (v == null) {
                return true;
            }
            if (this.id == v.id) {
                return true;
            }
            return false;
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

        Mediana getMedianaProximaLivre(List<Mediana> medianas) {
            int capacidade_mediana, soma_cap_demanda;
            TreeMap<Double, List<Mediana>> listaDistancias = new TreeMap<>();
            int size = 0;
            Double distancia;
//          for (Mediana mediana : medianas) {
//              System.out.println(mediana.demanda_atual);
//          }
            for (Mediana mediana : medianas) {
                size++;
                distancia = this.calculaDistanciaVertices(mediana.vertice_mediana);
//                 distancia = this.distanciaVerticesHash.get(mediana.vertice_mediana.hashCode());
                if (mediana.vertice_mediana.id == this.id) {
                    capacidade_mediana = mediana.vertice_mediana.capacidade;
                    soma_cap_demanda = (mediana.demanda_atual + this.demanda);
                    if (capacidade_mediana >= soma_cap_demanda) {
                        mediana.demanda_atual = soma_cap_demanda;
                        mediana.lista_vertices.add(this);
                        mediana.qde_vertices++;
                        return mediana;
                    }
                }
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
            return null;
        }

        Double calculaDistanciaVertices(Vertice vertice) {
            return this.calculaPitagoras(this.posX, vertice.posX, this.posY, vertice.posY);
        }

        Double calculaPitagoras(int x1, int x2, int y1, int y2) {
            Double retorno = Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
            return retorno;
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

        TreeMap<Integer, List<Vertice>> readFile(String arquivo, int runcodes) throws IOException {
            TreeMap<Integer, List<Vertice>> listaV = new TreeMap<>(Collections.reverseOrder());
            ArrayList<Vertice> arrayV = new ArrayList<>();
            int ind = 0;
            int soma_demanda = 0;
            Vertice v = new Vertice();
            Scanner scan;
            if (runcodes == 1) {
                scan = new Scanner(System.in);
            } else {
                scan = new Scanner(new FileReader(Main.class.getResource(arquivo).getPath()));
            }
            qdePecas = scan.nextInt();
            qdeMedianas = scan.nextInt();

            scan.nextLine();

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
//            TreeMap<Integer, List<Vertice>> listaV = new TreeMap<>();
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
