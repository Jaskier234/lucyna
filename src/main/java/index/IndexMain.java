package main.java.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.NormsFieldExistsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.nio.file.Path;
import java.nio.file.Paths;

public class IndexMain {
    public static void main(String[] args) {
        Path indexPath = Paths.get(System.getProperty("user.home"));
        indexPath = indexPath.resolve(".index");

        Writer writer = new Writer(indexPath);

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
//                    writer.deleteAll();
                    break;
                case "--add":
                    // dodaje ścieżkę do katalogu
//                    System.out.println(args.length);
                    if(args.length == 2) {
//                        Writer writer = new Writer(indexPath);
                        writer.addDirectory(Paths.get(args[1]));
//                        writer.close();
                    }
                    else {
                        System.out.println("Niepoprawne polecenie(add)");
                    }
                    break;
                case "--rm":
                    // usuwa katalog z indeksu
                    if(args.length == 2) {
                        Term directory = new Term("directory", args[1]);
                        Term fileDirectory = new Term("filedirectory", args[1]);

//                        writer.deleteDirectory();
                    }
                    else {
                        System.out.println("Niepoprawne polecenie");
                    }
                    break;
                case "--reindex":
                    // ponownie indeksuje wszystkie pliki
                    break;
                case "--list":
                    // wypisuje wszystkie dodane katalogi
                    writer.commit();
                    Reader reader = new Reader(indexPath);
                    NormsFieldExistsQuery query = new NormsFieldExistsQuery("directory");

                    TopDocs results = reader.search(query, 0);

                    for(ScoreDoc scoreDoc : results.scoreDocs) {
                        Document document = reader.getDocument(scoreDoc.doc);
//                        document.get("directory");
                        System.out.println(document.get("directory"));
                    }

                    break;
                default:
                    System.out.println("Niepoprawne polecenie");
            }
        }
//        writer.close();

    }
}
