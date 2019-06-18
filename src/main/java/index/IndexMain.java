package main.java.index;

import java.nio.file.Paths;

public class IndexMain {
    public static void main(String[] args) {

        Writer index = new Writer(Paths.get(System.getProperty("user.home") + ".index"));

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
                    index.deleteAll();
                    break;
                case "--add":
                    // dodaje ścieżkę do katalogu
                    if(args.length != 2) {
                        index.addDirectory(Paths.get(args[1]));
                    }
                    else {
                        System.out.println("Niepoprawne polecenie");
                    }
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
