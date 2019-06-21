package main.java.searcher;

public class SearcherMain {
    public static void main(String[] args) {
        Searcher searcher = new Searcher();

        while(true){
            searcher.parseInput();
            searcher.evaluate();
        }
    }
}
