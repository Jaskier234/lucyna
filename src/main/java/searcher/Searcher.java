package main.java.searcher;

import main.java.index.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

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
                if(args[1].equals("en")) {
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
                if(args[1].equals("off")) {
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
                if(args[1].equals("off")) {
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

        switch(config.queryType) {
            case TERM:
                break;
            case PHRASE:
                break;
            case FUZZY:
                break;
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
