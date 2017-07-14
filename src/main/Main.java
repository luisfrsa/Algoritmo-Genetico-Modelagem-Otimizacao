package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    static List<Vertice> sucessores = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("teste");
        Main.readFile();
    }

    static void readFile() throws IOException {
        Main.readFile("src/main/caso1.txt");
    }

    static void readFile(String arquivo) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(arquivo)));
        String lines[] = content.split("[\\r\\n]+");
        int ind = 0;
        for (String linha : lines) {
            String splits[] = linha.split(" ");
            for (String elem : splits) {
                Main.sucessores.add(new Vertice());
            }
        }
    }
}

class Vertice {

    int posX;
    int posY;
    int capacidade;
    int demanda;

    public Vertice(int posX, int posY, int capacidade, int demanda) {
        this.posX = posX;
        this.posY = posY;
        this.capacidade = capacidade;
        this.demanda = demanda;
    }

}
