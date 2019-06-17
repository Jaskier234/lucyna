package main.java.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.*;
import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Index {
    private Directory indexDirectory;
    private IndexWriterConfig config;
    private IndexWriter indexWriter;
    private Tika tika;

    public Index(Path indexPath) {
        tika = new Tika();

        try {
            indexDirectory = new MMapDirectory(indexPath);
            HashMap<String, Analyzer> analyzerPerField = new HashMap<>();
            // todo rozważyć inne analizatory
            analyzerPerField.put("eng", new EnglishAnalyzer());
            analyzerPerField.put("pol", new PolishAnalyzer());
            analyzerPerField.put("gen", new StandardAnalyzer());

            PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
            config = new IndexWriterConfig(analyzer);
            indexWriter = new IndexWriter(indexDirectory, config); // todo przenieść do addFile
        }
        catch (IOException e) {
            System.out.println("Nie udało się utworzyć indeksu(Konstruktor)");
        }
    }

    public void addDirectory(Path directory) {
        try {
            Files.walkFileTree(directory, new IndexFileVisitor(this));
        }
        catch(IOException e) {
            System.out.println("Nie udało się przejrzeć katalogu(addDirectory)");
        }

        Document directoryName = new Document();
        directoryName.add(new TextField());

        try {
            indexWriter = new IndexWriter(indexDirectory, config);
            indexWriter.addDocument()
        }
        catch(IOException e) {
            System.out.println("Nie udało się dodać katalogu do listy obserwowanych katalogów");
        }
    }

    public void addFile(Path file) {
        String parsedFileContent;
        try {
            try (InputStream input = Files.newInputStream(file)) {
                parsedFileContent = tika.parseToString(input);

//                System.out.println(parsedFileContent);

                Document document = new Document();

                document.add(new TextField("filename", file.toString(), Field.Store.YES));
                document.add(new TextField("pol", parsedFileContent, Field.Store.YES));

                try {
                    indexWriter = new IndexWriter(indexDirectory, config);
                    indexWriter.addDocument(document);
                }
                catch(IOException e) {
                    System.out.println("Nie udało się dodać pliku do indeksu");
                }
                finally {
                    indexWriter.close();
                }
            } catch (Exception e) { // todo wypisać więcej info
                System.out.println("Nie udało się przetworzyć pliku");
            }
        }
        catch(Exception e) {
            System.out.println("Nie udało się odczytać pliku");
        }
    }

    public void deleteFile(Path file) {

    }
}
