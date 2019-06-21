package main.java.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
//            e.printStackTrace();
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

    ////////////
    public List<String> analyze(String text, Analyzer analyzer) throws IOException{
        List<String> result = new ArrayList<String>();
        TokenStream tokenStream = analyzer.tokenStream("polish", text);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while(tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }
    /////////////

    public void addFile(Path file, String fileContent, String langField) {
        System.out.println(">>>>>>>>>>>> addFile <<<<<<<<<<<<");
        try {
            Document document = new Document();

            document.add(new TextField(FILE_DIR, file.toAbsolutePath().normalize().toString(), Field.Store.YES));
            document.add(new TextField(FILE_NAME, file.getFileName().toString(), Field.Store.YES));
            System.out.println(file.toAbsolutePath().normalize().toString());

            if(langField.equals("pl"))
                document.add(new TextField(POL, fileContent, Field.Store.YES));
            if(langField.equals("en"))
                document.add(new TextField(ENG, fileContent, Field.Store.YES));
            else
                document.add(new TextField(GEN, fileContent, Field.Store.YES));

            ///////////////
            List<String> tokens = analyze(fileContent, new PolishAnalyzer());
            System.out.print(file.toString() + ": ");
            for(String s : tokens) {
                System.out.print(s + ", ");
            }
            System.out.println();
            /////////////////

            try {
                indexWriter.addDocument(document);
            }
            catch(IOException e) {
                System.out.println("Nie udało się dodać pliku do indeksu");
            }
        }
        catch(Exception e) {
//            System.out.println("Nie udało się odczytać pliku");
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
//            indexWriter.forceMergeDeletes();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void deleteFile(Path file) {

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
