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
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Writer {
    public static String ENG = "english";
    public static String POL = "polish";
    public static String GEN = "generic";
    public static String FILE_NAME = "filename";
    public static String FILE_DIR = "filedirectory";
    public static String DIR = "directory";

    private Path indexPath;
    private FSDirectory indexDirectory;
    private IndexWriter indexWriter;
    private PerFieldAnalyzerWrapper analyzer;

    public Writer(Path path) {
        indexPath = path;
        try {
            indexDirectory = new SimpleFSDirectory(indexPath);

            HashMap<String, Analyzer> analyzerPerField = new HashMap<>();
            analyzerPerField.put(ENG, new EnglishAnalyzer());
            analyzerPerField.put(POL, new PolishAnalyzer());
            analyzerPerField.put(GEN, new StandardAnalyzer());
            analyzerPerField.put(FILE_NAME, new StandardAnalyzer());
            analyzerPerField.put(FILE_DIR, new KeywordAnalyzer());
            analyzerPerField.put(DIR, new KeywordAnalyzer());

            analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);

            indexWriter = new IndexWriter(indexDirectory, new IndexWriterConfig(analyzer));
        }
        catch (IOException e) {
            System.err.println("Nie udało się otworzyć indeksu(Writer)");
            System.exit(1);
        }
    }

    public void addDirectory(Path directory) {
        // dodanie katalogu do listy obserwowanych
        try {
            Document directoryDocument = new Document();
            directoryDocument.add(new TextField(DIR, directory.toString(), Field.Store.YES));
            indexWriter.addDocument(directoryDocument);
        }
        catch (IOException e) {
            System.err.println("Nie udało się dodać katalogu do obserwowanych");
            // jeśli nie udało się dodać katalogu do obserwowanych, to nie dodajemy plików do indeksu
            return ;
        }

        // dodanie plików do indeksu
        addDirectoryFiles(directory);
    }


    public void addDirectoryFiles(Path directory) {
        try {
            Files.walkFileTree(directory, new IndexFileVisitor(this));
        }
        catch(IOException e) {
            System.err.println("Nie udało się przejrzeć katalogu(addDirectory)");
        }
    }

    public void addFile(Path file, String fileContent, String langField) {
        try {
            Document document = new Document();

            document.add(new TextField(FILE_DIR, file.toAbsolutePath().normalize().toString(), Field.Store.YES));
            document.add(new TextField(FILE_NAME, file.getFileName().toString(), Field.Store.YES));

            if(langField.equals("pl"))
                document.add(new TextField(POL, fileContent, Field.Store.YES));
            else if(langField.equals("en"))
                document.add(new TextField(ENG, fileContent, Field.Store.YES));
            else
                document.add(new TextField(GEN, fileContent, Field.Store.YES));

            try {
                indexWriter.addDocument(document);
            }
            catch(IOException e) {
                System.err.println("Nie udało się dodać pliku do indeksu");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Usuwa katalog z obserwowania oraz pliki z indeksu.
    // Jeśli katalog nie był obserwowany tylko usuwa pliki.
    public void deleteDirectory(Path directory) {
        Term directoryTerm = new Term(DIR, directory.toString());
        Query query = new TermQuery(directoryTerm);

        Term fileDirectoryTerm = new Term(FILE_DIR, directory.toString());
        Query fileQuery = new PrefixQuery(fileDirectoryTerm);

        try {
            indexWriter.deleteDocuments(query);
            indexWriter.deleteDocuments(fileQuery);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll() {
        try {
            indexWriter.deleteAll();
        }
        catch(IOException e) {
            System.err.println("Nie udało się usunąć dokumentów z indeksu");
        }
    }

    public void close() {
        try {
            indexWriter.close();
        }
        catch(IOException e) {
            System.err.println("Nie udało się zamknąć writera");
        }
    }

    public void commit() {
        try {
            indexWriter.commit();
        }
        catch (IOException e) {
            System.err.println("Zapis do indeksu nie powiódł się");
        }
    }
}
