package test;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main
{
    private static StandardAnalyzer analyzer = new StandardAnalyzer();
    private static RAMDirectory memoryIndex = new RAMDirectory();

    public static void main(String[] args) throws IOException, TikaException, ParseException {


        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter writter = new IndexWriter(memoryIndex, indexWriterConfig);
        Document document = new Document();

        document.add(new TextField("title", "tytuł", Field.Store.YES));
        document.add(new TextField("body", "Jam jest posąg człowieka na posągu świata!", Field.Store.YES));

        writter.addDocument(document);
        writter.close();

        List<Document> documents = searchIndex("body", "posąg");
        System.out.println(documents.size());
        for(int i=0; i<documents.size(); i++)
            System.out.println(documents.get(i));
    }

    public static List<Document> searchIndex(String inField, String queryString) throws IOException, ParseException {
        Query query = new QueryParser(inField, analyzer)
                .parse(queryString);

        IndexReader indexReader = DirectoryReader.open(memoryIndex);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 10);
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }

        return documents;
    }

    public static String parseFile(String s) throws IOException, TikaException {
        Tika tika = new Tika();
        Path path = Paths.get(s);
        System.out.println(path.toAbsolutePath().toString());
        try (InputStream stream = Files.newInputStream(path)) {
            return tika.parseToString(stream);
        }

    }
}