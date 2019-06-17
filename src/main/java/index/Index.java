package main.java.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Index {
    private Directory indexDirectory;
//    private Analyzer polishAnalyzer = new PolishAnalyzer();
//    private Analyzer englishAnalyzer = new EnglishAnalyzer();
//    private Analyzer genericAnalyzer = new StandardAnalyzer();
    private IndexWriter polishIndexWriter;
    private IndexWriter englishIndexWriter;
    private IndexWriter genericIndexWriter;


    public Index(Path indexPath) {
        try {
            indexDirectory = new MMapDirectory(indexPath);
//            polishIndexWriter = new IndexWriter(indexDirectory, new IndexWriterConfig(new PolishAnalyzer()));
            englishIndexWriter = new IndexWriter(indexDirectory, new IndexWriterConfig(new EnglishAnalyzer()));
//            genericIndexWriter = new IndexWriter(indexDirectory, new IndexWriterConfig(new StandardAnalyzer()));
        }
        catch (IOException e) {
            System.out.println("Nie udało się utworzyć indeksu(Konstruktor)");
        }

    }

    public void addDirectory(Path directory) {
        try {
            Files.walkFileTree(directory, new IndexFileVisitor());

        }
        catch(IOException e) {
            System.out.println("Nie udało się przejrzeć katalogu(addDirectory)");
        }
    }

    public void addFile(Path file) {

    }

    public void deleteFile(Path file) {

    }
}
