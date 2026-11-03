import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class WordNet {
    private final Map<String, List<Integer>> nouns = new TreeMap<>();
    private final String[] synsets;
    private final SAP paths;
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new IllegalArgumentException("null file");
        List<String> sets = new ArrayList<>();
        In input = new In(synsets);
        while (input.hasNextLine()) {
            String[] fields = input.readLine().split(",", 3);
            int id = Integer.parseInt(fields[0]);
            if (id != sets.size()) throw new IllegalArgumentException("nonsequential synset id");
            sets.add(fields[1]);
            for (String noun : fields[1].split(" "))
                nouns.computeIfAbsent(noun, key -> new ArrayList<>()).add(id);
        }
        input.close();
        this.synsets = sets.toArray(new String[0]);
        Digraph graph = new Digraph(sets.size());
        input = new In(hypernyms);
        while (input.hasNextLine()) {
            String[] fields = input.readLine().split(",");
            int from = Integer.parseInt(fields[0]);
            for (int i = 1; i < fields.length; i++) graph.addEdge(from, Integer.parseInt(fields[i]));
        }
        input.close();
        validateRootedDag(graph);
        paths = new SAP(graph);
    }
    private static void validateRootedDag(Digraph graph) {
        int[] incoming = new int[graph.V()];
        int[] queue = new int[graph.V()];
        int head = 0, tail = 0, roots = 0;
        for (int v = 0; v < graph.V(); v++) {
            incoming[v] = graph.indegree(v);
            if (incoming[v] == 0) queue[tail++] = v;
            if (graph.outdegree(v) == 0) roots++;
        }
        while (head < tail) {
            int v = queue[head++];
            for (int w : graph.adj(v)) if (--incoming[w] == 0) queue[tail++] = w;
        }
        if (roots != 1 || head != graph.V()) throw new IllegalArgumentException("not a rooted DAG");
    }
    public Iterable<String> nouns() { return Collections.unmodifiableSet(nouns.keySet()); }
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("null noun");
        return nouns.containsKey(word);
    }
    private List<Integer> senses(String word) {
        if (!isNoun(word)) throw new IllegalArgumentException("unknown noun: " + word);
        return nouns.get(word);
    }
    public int distance(String nounA, String nounB) { return paths.length(senses(nounA), senses(nounB)); }
    public String sap(String nounA, String nounB) { return synsets[paths.ancestor(senses(nounA), senses(nounB))]; }
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        StdOut.println(wordnet.isNoun(args[2]));
        StdOut.println(wordnet.distance(args[2], args[3]));
        StdOut.println(wordnet.sap(args[2], args[3]));
    }
}
