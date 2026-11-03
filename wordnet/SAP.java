import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;
import java.util.Collections;

public class SAP {
    private final Digraph graph;
    public SAP(Digraph graph) {
        if (graph == null) throw new IllegalArgumentException("null graph");
        this.graph = new Digraph(graph);
    }
    private int[] distances(Iterable<Integer> sources) {
        if (sources == null) throw new IllegalArgumentException("null sources");
        int[] distance = new int[graph.V()];
        Arrays.fill(distance, -1);
        int[] queue = new int[graph.V()];
        int head = 0, tail = 0;
        for (Integer source : sources) {
            if (source == null || source < 0 || source >= graph.V())
                throw new IllegalArgumentException("invalid vertex");
            if (distance[source] == -1) {
                distance[source] = 0;
                queue[tail++] = source;
            }
        }
        while (head < tail) {
            int v = queue[head++];
            for (int w : graph.adj(v)) if (distance[w] == -1) {
                distance[w] = distance[v]+1;
                queue[tail++] = w;
            }
        }
        return distance;
    }
    private int[] search(Iterable<Integer> v, Iterable<Integer> w) {
        int[] a = distances(v), b = distances(w);
        int ancestor = -1, length = Integer.MAX_VALUE;
        for (int i = 0; i < graph.V(); i++)
            if (a[i] >= 0 && b[i] >= 0 && a[i]+b[i] < length) {
                ancestor = i;
                length = a[i]+b[i];
            }
        return new int[]{ancestor == -1 ? -1 : length, ancestor};
    }
    public int length(int v, int w) { return length(Collections.singletonList(v), Collections.singletonList(w)); }
    public int ancestor(int v, int w) { return ancestor(Collections.singletonList(v), Collections.singletonList(w)); }
    public int length(Iterable<Integer> v, Iterable<Integer> w) { return search(v, w)[0]; }
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) { return search(v, w)[1]; }
    public static void main(String[] args) {
        SAP sap = new SAP(new Digraph(new In(args[0])));
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt(), w = StdIn.readInt();
            StdOut.printf("length = %d, ancestor = %d%n", sap.length(v,w), sap.ancestor(v,w));
        }
    }
}
