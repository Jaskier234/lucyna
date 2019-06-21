package main.java.searcher;

import main.java.index.Reader;
import main.java.index.Writer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.util.QueryBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

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
    Formatter formatter;

    public Searcher() {
        config = new SearcherConfig();

        formatter = new Formatter() {
            @Override
            public String highlightTerm(String s, TokenGroup tokenGroup) {
                if(tokenGroup.getTotalScore() == 0) {
                    return s;
                }

                return new AttributedStringBuilder().append("")
                        .style(AttributedStyle.DEFAULT.bold())
                        .append(s)
                        .toAnsi();
            }
        };
    }

    public void parseInput()
    {
        System.out.print(">");
        Scanner scanner = new Scanner(System.in);

        String line = scanner.nextLine();
        queryString = null;
        correctCommand = true;

        String[] args = line.split(" ");

        if(args.length <= 0 || line.equals("")) {
            correctCommand = false;
            return;
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

        Path indexPath = Paths.get(System.getProperty("user.home"));
        indexPath = indexPath.resolve(".index");
        Reader reader = new Reader(indexPath);

        Query query = null;
        List<String> analyzedLanguageString;
        List<String> analyzedStandardString;

        String field;
        if(config.language == SearcherConfig.Language.PL) {
            field = Writer.POL;
        }
        else {
            field = Writer.ENG;
        }

        try {
            if(config.language == SearcherConfig.Language.PL) {
                analyzedLanguageString = analyze(queryString, new PolishAnalyzer());
            }
            else {
                analyzedLanguageString = analyze(queryString, new EnglishAnalyzer());
            }
            analyzedStandardString = analyze(queryString, new StandardAnalyzer());
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        QueryBuilder builder;
        switch(config.queryType) {
            case TERM:
                if(analyzedLanguageString.size() > 1) {
                    System.err.println("Podana fraza składa się z więcej niż jednego termu. Lepiej użyć %phrase");
                }

                Term languageTerm = new Term(field, analyzedLanguageString.get(0));
                Term genericTerm = new Term(Writer.GEN, analyzedStandardString.get(0));
                Term filenameTerm = new Term(Writer.FILE_NAME, analyzedStandardString.get(0));

                query = new BooleanQuery.Builder()
                        .add(new TermQuery(languageTerm), BooleanClause.Occur.SHOULD)
                        .add(new TermQuery(genericTerm), BooleanClause.Occur.SHOULD)
                        .add(new TermQuery(filenameTerm), BooleanClause.Occur.SHOULD)
                        .build();
                break;
            case PHRASE:
                if(config.language == SearcherConfig.Language.PL) {
                    builder = new QueryBuilder(new PolishAnalyzer());
                }
                else {
                    builder = new QueryBuilder(new EnglishAnalyzer());
                }

                query = new BooleanQuery.Builder()
                        .add(builder.createPhraseQuery(field, queryString), BooleanClause.Occur.SHOULD)
                        .add(builder.createPhraseQuery(Writer.GEN, queryString), BooleanClause.Occur.SHOULD)
                        .build();
                break;
            case FUZZY:
                query = new BooleanQuery.Builder()
                        .add(new FuzzyQuery(new Term(field, queryString)), BooleanClause.Occur.SHOULD)
                        .add(new FuzzyQuery(new Term(Writer.GEN, queryString)), BooleanClause.Occur.SHOULD)
                        .build();
                break;
        }


        Formatter formatter = new Formatter() {
            @Override
            public String highlightTerm(String s, TokenGroup tokenGroup) {
                if(tokenGroup.getTotalScore() == 0) {
                    return s;
                }

                if(config.color)
                    return new AttributedStringBuilder()
                            .style(AttributedStyle.BOLD.foreground(AttributedStyle.GREEN))
                            .append(s)
                            .toAnsi();
                else
                    return new AttributedStringBuilder()
                            .style(AttributedStyle.DEFAULT.bold())
                            .append(s)
                            .toAnsi();
            }
        };

        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, scorer);

        highlighter.setTextFragmenter(new SimpleFragmenter());

        TopDocs results = reader.search(query, config.limit);
        System.out.println("File count:" + results.scoreDocs.length);

        for(ScoreDoc scoreDoc : results.scoreDocs) {
            Document document = reader.getDocument(scoreDoc.doc);

            System.out.println(new AttributedStringBuilder()
                    .style(AttributedStyle.DEFAULT.bold())
                    .append(document.get(Writer.FILE_DIR))
                    .toAnsi());

            String text;
            TokenStream stream;
            if(document.get(Writer.GEN) != null) {
                text = document.get(Writer.GEN);

                Analyzer genericAnalyzer = new StandardAnalyzer();
                stream = genericAnalyzer.tokenStream(Writer.GEN, text);
            }
            else if(document.get(field) != null) {
                text = document.get(field);

                Analyzer languageAnalyzer;
                if(config.language == SearcherConfig.Language.PL) {
                    languageAnalyzer = new PolishAnalyzer();
                }
                else {
                    languageAnalyzer = new EnglishAnalyzer();
                }

                stream = languageAnalyzer.tokenStream(field, text);
            }
            else {
                continue;
            }

            String[] context = new String[0];

            try {
                context = highlighter.getBestFragments(stream, text, 10);
            }
            catch (IllegalArgumentException e) {
                System.err.println("Zapytanie do FuzzyQuery musi mieć conajmniej 4 znaki");
            }
            catch (Exception e) {
                System.err.println("Problem z wypisaniem kontekstu");
            }

            if(config.details) {
                for (String s: context) {
                    System.out.println(s);
                }
            }
        }
        reader.close();
    }

    public static List<String> analyze(String text, Analyzer analyzer) throws IOException {
        List<String> result = new ArrayList<String>();
        TokenStream tokenStream = analyzer.tokenStream("field", text);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while(tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }
}
