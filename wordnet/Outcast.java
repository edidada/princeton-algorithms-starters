import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordnet;
    public Outcast(WordNet wordnet) {
        if (wordnet == null) throw new IllegalArgumentException("null WordNet");
        this.wordnet = wordnet;
    }
    public String outcast(String[] nouns) {
        if (nouns == null || nouns.length == 0) throw new IllegalArgumentException("empty nouns");
        for (String noun : nouns) if (!wordnet.isNoun(noun)) throw new IllegalArgumentException("unknown noun");
        long[] totals = new long[nouns.length];
        for (int i = 0; i < nouns.length; i++) for (int j = i+1; j < nouns.length; j++) {
            int distance = wordnet.distance(nouns[i], nouns[j]);
            totals[i] += distance;
            totals[j] += distance;
        }
        int best = 0;
        for (int i = 1; i < nouns.length; i++) if (totals[i] > totals[best]) best = i;
        return nouns[best];
    }
    public static void main(String[] args) {
        Outcast outcast = new Outcast(new WordNet(args[0], args[1]));
        for (int i = 2; i < args.length; i++) {
            In input = new In(args[i]);
            StdOut.println(args[i] + ": " + outcast.outcast(input.readAllStrings()));
            input.close();
        }
    }
}
