package main.java.searcher;

public class SearcherConfig {
    public enum Language {
        PL, ENG;
    }
    public Language language;

    public boolean details;
    public boolean color;
    public int limit;

    public enum QueryType {
        TERM, PHRASE, FUZZY;
    }
    public QueryType queryType;

    public SearcherConfig()
    {
        language = Language.ENG;
        details = false;
        color = false;
        limit = Integer.MAX_VALUE;
        queryType = QueryType.TERM;
    }


}
