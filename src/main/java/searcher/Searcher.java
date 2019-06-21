package main.java.searcher;

import main.java.index.Reader;
import main.java.index.Writer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
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
import java.util.Scanner;

public class Searcher {
    private SearcherConfig config;
    private boolean correctCommand;
    private String queryString;
    private Reader reader;

    public Searcher() {
        config = new SearcherConfig();

        Path indexPath = Paths.get(System.getProperty("user.home"));
        indexPath = indexPath.resolve(".index");

        reader = new Reader(indexPath);

    }

    public void parseInput()
    {
        System.out.print(">");
        Scanner scanner = new Scanner(System.in);

        String line = scanner.nextLine();
        queryString = null;
        correctCommand = true;

        String[] args = line.split(" ");

        if(args.length > 2) {
            correctCommand = false;
        }

        switch(args[0]) {
            case "%lang":
                if(args[1].equals("pl")) {
                    config.language = SearcherConfig.Language.PL;
                }
                else if(args[1].equals("en")) {
                    config.language = SearcherConfig.Language.ENG;
                }
                else {
                    correctCommand = false;
                }
                break;
            case "%details":
                if(args[1].equals("on")) {
                    config.details = true;
                }
                else if(args[1].equals("off")) {
                    config.details = false;
                }
                else {
                    correctCommand = false;
                }
                break;
            case "%color":
                if(args[1].equals("on")) {
                    config.color = true;
                }
                else if(args[1].equals("off")) {
                    config.color = false;
                }
                else {
                    correctCommand = false;
                }
                break;
            case "%limit":
                try {
                    int num = Integer.parseInt(args[1]);
                    if(num == 0) {
                        config.limit = Integer.MAX_VALUE;
                    }
                    else {
                        config.limit = num;
                    }
                }
                catch (NumberFormatException e) {
                    correctCommand = false;
                }
                break;
            case "%term":
                if(args.length <= 1)
                    config.queryType = SearcherConfig.QueryType.TERM;
                else
                    correctCommand = false;
                break;
            case "%phrase":
                if(args.length <= 1)
                    config.queryType = SearcherConfig.QueryType.PHRASE;
                else
                    correctCommand = false;
                break;
            case "%fuzzy":
                if(args.length <= 1)
                    config.queryType = SearcherConfig.QueryType.FUZZY;
                else
                    correctCommand = false;
                break;
            default:
                queryString = line;
        }
    }

    public void evaluate() {
        if(!correctCommand) {
            System.out.println("Niepoprawna komenda");
            return;
        }

        if(queryString == null)
            return;

        Query query = null;
        List<String> analyzedString;
        try {
            analyzedString = analyze(queryString, new PolishAnalyzer());

        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        switch(config.queryType) {
            case TERM:
                if(config.language == SearcherConfig.Language.PL) { // todo dodać wyszukiwanie w generycznym
                    Term term = new Term(Writer.POL, analyzedString.get(0));
                    if(analyzedString.size() > 1){}
                        // lepiej użyć phrase query
                    query = new TermQuery(term);
                }
                else if(config.language == SearcherConfig.Language.ENG) {
                    Term term = new Term(Writer.ENG, analyzedString.get(0));
                    if(analyzedString.size() > 1){}
                          // lepiej użyć phrase query
                    query = new TermQuery(term);
                }
                break;
            case PHRASE:

                break;
            case FUZZY:
                break;
        }

        TopDocs results = reader.search(query, config.limit);
        System.out.println(results.scoreDocs.length);
        for(ScoreDoc scoreDoc : results.scoreDocs) {
            Document document = reader.getDocument(scoreDoc.doc);
            System.out.println(document.get(Writer.FILE_DIR));
        }
    }

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
}
