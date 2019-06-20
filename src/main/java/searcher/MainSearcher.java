package main.java.searcher;

import main.java.index.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MainSearcher {

    public static List<String> analyze(String text, Analyzer analyzer) throws IOException {
        List<String> result = new ArrayList<String>();
        TokenStream tokenStream = analyzer.tokenStream("polish", text);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while(tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }

    public static void main(String[] args) throws IOException {



        Path indexPath = Paths.get(System.getProperty("user.home"));
        indexPath = indexPath.resolve(".index");

        Reader reader = new Reader(indexPath);

        String text = "treść pliku";

        List<String> analyzed = analyze(text, new PolishAnalyzer());

        System.out.println(analyzed.get(0));

        Term term = new Term("polish", analyzed.get(0));
        Query query = new TermQuery(term);

        TopDocs results = reader.search(query, Integer.MAX_VALUE);
        System.out.println(results.scoreDocs.length);
        for(ScoreDoc scoreDoc : results.scoreDocs) {
            Document document = reader.getDocument(scoreDoc.doc);
            System.out.println(document.get("filename"));
        }
    }
}
