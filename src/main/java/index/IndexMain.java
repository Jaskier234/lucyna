package main.java.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.NormsFieldExistsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IndexMain {
    public static void main(String[] args) {
        Path indexPath = Paths.get(System.getProperty("user.home"));
        indexPath = indexPath.resolve(".index");

        Writer writer = new Writer(indexPath);

        writer.commit();
        Reader reader = new Reader(indexPath);

        if(args.length == 0) {
            System.out.println("skanuję foldery...");

            try {
                WatchDir watcher = new WatchDir(writer);

                // dodanie katalogów do watchera
                NormsFieldExistsQuery directoryQuery = new NormsFieldExistsQuery(Writer.DIR);

                TopDocs directoryResults = reader.search(directoryQuery, Integer.MAX_VALUE);

                for(ScoreDoc scoreDoc : directoryResults.scoreDocs) {
                    Document document = reader.getDocument(scoreDoc.doc);
                    watcher.registerAll(Paths.get(document.get(Writer.DIR)));
                }

                // uruchomienie obserwowania kolejki zdarzeń
                watcher.processEvents();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else {
            // wykonaj polecenie
            switch(args[0]) {
                case "--purge":
                    // usuwa wszystkie dokumenty z indeksu
                    writer.deleteAll();
                    break;
                case "--add":
                    // dodaje ścieżkę do katalogu
//                    System.out.println(args.length);
                    if(args.length == 2) {
                        Path directory = Paths.get(args[1]);
                        directory = directory.toAbsolutePath().normalize();
                        writer.addDirectory(directory);
                    }
                    else {
                        System.out.println("Niepoprawne polecenie(add)");
                    }
                    break;
                case "--rm":
                    // usuwa katalog z indeksu
                    if(args.length == 2) {
                        Path directory = Paths.get(args[1]);
                        directory = directory.toAbsolutePath().normalize();
                        writer.deleteDirectory(directory);
                    }
                    else {
                        System.out.println("Niepoprawne polecenie");
                    }
                    break;
                case "--reindex":
                    // ponownie indeksuje wszystkie pliki

                    NormsFieldExistsQuery fileQuery = new NormsFieldExistsQuery(Writer.DIR);
                    TopDocs fileResults = reader.search(fileQuery, Integer.MAX_VALUE);

                    writer.deleteAll();

                    for(ScoreDoc scoreDoc : fileResults.scoreDocs) {
                        Document document = reader.getDocument(scoreDoc.doc);
                        Path directoryPath = Paths.get(document.get(Writer.DIR));
                        writer.addDirectory(directoryPath);
                    }
                    break;
                case "--list":
                    // wypisuje wszystkie dodane katalogi
//                    writer.commit();
                    NormsFieldExistsQuery directoryQuery = new NormsFieldExistsQuery(Writer.DIR);

                    TopDocs directoryResults = reader.search(directoryQuery, Integer.MAX_VALUE);

                    for(ScoreDoc scoreDoc : directoryResults.scoreDocs) {
                        Document document = reader.getDocument(scoreDoc.doc);
                        System.out.println(document.get(Writer.DIR));
                    }

                    break;
                default:
                    System.out.println("Niepoprawne polecenie");
            }
        }
        writer.close();
        reader.close();
    }
}
