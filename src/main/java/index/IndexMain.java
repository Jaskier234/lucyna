package main.java.index;

import java.nio.file.Paths;

public class IndexMain {
    public static void main(String[] args) {

        Index index = new Index(Paths.get("./"));
        index.addDirectory(Paths.get("/home/artur/Dokumenty/lucyna"));

        if(args.length == 0) {
            System.out.println("skanuję foldery...");

            while(true) {
                // skanownaie folderów w poszukiwaniu zmian
            }
        }
        else {
            // wykonaj polecenie
            switch(args[0]) {
                case "--purge":
                    // usuwa wszystkie dokumenty z indeksu
                    break;
                case "--add":
                    // dodaje ścieżkę do katalogu
                    break;
                case "--rm":
                    // usuwa katalog z indeksu
                    break;
                case "--reindex":
                    // ponownie indeksuje wszystkie pliki
                    break;
                case "--list":
                    // wypisuje wszystkie dodane katalogi
                    break;
                default:
                    System.out.println("Niepoprawne polecenie");
            }
        }


    }
}
