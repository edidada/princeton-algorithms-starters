import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/** Uniform k-token subset of an input stream, with O(k) retained items. */
public final class Permutation {
    private Permutation() { }

    public static void main(String[] args) {
        if (args.length != 1) throw new IllegalArgumentException("Usage: Permutation k");
        int k = Integer.parseInt(args[0]);
        if (k < 0) throw new IllegalArgumentException("Negative k");
        RandomizedQueue<String> reservoir = new RandomizedQueue<>();
        int seen = 0;
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            seen++;
            if (reservoir.size() < k) reservoir.enqueue(item);
            else if (k > 0 && StdRandom.uniform(seen) < k) {
                reservoir.dequeue();
                reservoir.enqueue(item);
            }
        }
        if (seen < k) throw new IllegalArgumentException("Not enough input tokens");
        while (!reservoir.isEmpty()) StdOut.println(reservoir.dequeue());
    }
}
