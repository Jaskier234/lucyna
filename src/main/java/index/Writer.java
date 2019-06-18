package main.java.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Writer {
    private Directory indexDirectory;
    private IndexWriter indexWriter;
    private PerFieldAnalyzerWrapper analyzer;

    public Writer(Path indexPath) {

        try {
            indexDirectory = new MMapDirectory(indexPath);

            HashMap<String, Analyzer> analyzerPerField = new HashMap<>();
            // todo rozważyć inne analizatory
            analyzerPerField.put("english", new EnglishAnalyzer());// todo dodać pole nazwa
            analyzerPerField.put("polish", new PolishAnalyzer());
            analyzerPerField.put("generic", new StandardAnalyzer());
            analyzerPerField.put("directory", new KeywordAnalyzer());

            analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
        }
        catch (IOException e) {
            System.out.println("Nie udało się otworzyć indeksu(Konstruktor)(Writer)");
        }
    }

    public void addDirectory(Path directory) {
        try {
            Files.walkFileTree(directory, new IndexFileVisitor(this));
        }
        catch(IOException e) {
            System.out.println("Nie udało się przejrzeć katalogu(addDirectory)");
        }

        try {
            indexWriter = new IndexWriter(indexDirectory, new IndexWriterConfig(analyzer));

            Document directoryDocument = new Document();
            directoryDocument.add(new TextField("directory", directory.toString(), Field.Store.YES));
            indexWriter.addDocument(directoryDocument);

            indexWriter.close();
        }
        catch (IOException e) {
            System.out.println("Nie udało się dodać katalogu do obserwowanych");
        }
    }

    public void addFile(Path file, String fileContent) {
        try {
            Document document = new Document();

            document.add(new TextField("filename", file.toString(), Field.Store.YES));
            document.add(new TextField("pol", fileContent, Field.Store.YES));

            try {
                indexWriter.addDocument(document);
            }
            catch(IOException e) {
                System.out.println("Nie udało się dodać pliku do indeksu");
            }
        }
        catch(Exception e) {
            System.out.println("Nie udało się odczytać pliku");
        }
    }

    public void deleteFile(Path file) {

    }

    public void deleteAll() {
        try {
            indexWriter.deleteAll();
        }
        catch(IOException e) {
            System.out.println("Nie udało się usunąć indeksu");
        }
    }

    public void close() {
        try {
            indexWriter.close();
        }
        catch(IOException e) {
            System.out.println("Nie udało się zamknąć indexWritera");
        }
    }
}
