package main.java.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Reader {
    private Directory indexDirectory;
//    private IndexReader indexReader;
    private IndexSearcher indexSearcher;

    public Reader(Path indexPath) {
        try {
            indexDirectory = new MMapDirectory(indexPath);
            IndexReader indexReader = DirectoryReader.open(indexDirectory);
            indexSearcher = new IndexSearcher(indexReader);
        }
        catch(IOException e) {
            System.out.println("Nie udało się otworzyć indeksu(Konstruktor)(Reader)");
        }
    }

    public List<Document> search(Query query, int limit) {

        List<Document> results = new ArrayList<>();
        try {
            TopDocs topDocs = indexSearcher.search(query, limit);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                results.add(indexSearcher.doc(scoreDoc.doc));
            }
        }
        catch (IOException e) {
            System.out.println("Błąd wyszukiwania");
        }

        return results;
    }
}
