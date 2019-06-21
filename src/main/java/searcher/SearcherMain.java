package main.java.searcher;

import main.java.index.Reader;
import main.java.index.Writer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SearcherMain {



    public static void main(String[] args) throws IOException{
        Searcher searcher = new Searcher();

        while(true){
            searcher.parseInput();
            searcher.evaluate();
        }


//        Path indexPath = Paths.get(System.getProperty("user.home"));
//        indexPath = indexPath.resolve(".index");
//
//        Reader reader = new Reader(indexPath);
//
//        String text = "polska";
//
//        List<String> analyzed = analyze(text, new PolishAnalyzer());
//
//        System.out.println(analyzed.get(0));
//
//        Term term = new Term("polish", analyzed.get(0));
//        Query query = new TermQuery(term);
//
////        Term fileDirectoryTerm = new Term(Writer.FILE_DIR, "/home/artur/Dokumenty/Lucyna/asdf/globus/globus.dfg");
////        Query query = new PrefixQuery(fileDirectoryTerm);
//
//        TopDocs results = reader.search(query, Integer.MAX_VALUE);
//        System.out.println(results.scoreDocs.length);
//        for(ScoreDoc scoreDoc : results.scoreDocs) {
//            Document document = reader.getDocument(scoreDoc.doc);
//            System.out.println(document.get("filename"));
//        }

    }
}
