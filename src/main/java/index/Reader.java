package main.java.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Reader {
    private FSDirectory indexDirectory;
    private IndexReader indexReader;
    private IndexSearcher indexSearcher;

    public Reader(Path indexPath) {
        try {
            indexDirectory = new SimpleFSDirectory(indexPath);
            indexReader = DirectoryReader.open(indexDirectory);
            indexSearcher = new IndexSearcher(indexReader);
        }
        catch(IOException e) {
            System.out.println("Nie udało się otworzyć indeksu(Konstruktor)(Reader)");
        }
    }

    public TopDocs search(Query query, int limit) {

        List<Document> results = new ArrayList<>();
        try {
            return indexSearcher.search(query, limit);
        }
        catch (IOException e) {
            System.out.println("Błąd wyszukiwania");
            return null;
        }
    }

    public Document getDocument(int documentId) {
        try {
            return indexSearcher.doc(documentId);
        }
        catch (IOException e) {
            System.out.println("Nie udało się zwrócić dokumentu");
            return null;
        }
    }

    public void close()
    {
        try {
            indexReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
