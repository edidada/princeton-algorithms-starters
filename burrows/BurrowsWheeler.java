import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    public static void transform() {
        String input = BinaryStdIn.isEmpty() ? "" : BinaryStdIn.readString();
        int n = input.length();
        CircularSuffixArray suffixes = new CircularSuffixArray(input);
        int first = 0;
        for (int i = 0; i < n; i++) if (suffixes.index(i) == 0) first = i;
        BinaryStdOut.write(first);
        for (int i = 0; i < n; i++) BinaryStdOut.write(input.charAt((suffixes.index(i)+n-1)%n), 8);
        BinaryStdOut.close();
    }
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String last = BinaryStdIn.isEmpty() ? "" : BinaryStdIn.readString();
        int n = last.length();
        if ((n == 0 && first != 0) || (n > 0 && (first < 0 || first >= n)))
            throw new IllegalArgumentException("invalid first row");
        int[] counts = new int[257], next = new int[n];
        char[] sorted = new char[n];
        for (int i = 0; i < n; i++) counts[last.charAt(i)+1]++;
        for (int c = 1; c <= 256; c++) counts[c] += counts[c-1];
        for (int i = 0; i < n; i++) {
            char letter = last.charAt(i);
            int position = counts[letter]++;
            sorted[position] = letter;
            next[position] = i;
        }
        int row = first;
        for (int i = 0; i < n; i++) {
            BinaryStdOut.write(sorted[row], 8);
            row = next[row];
        }
        BinaryStdOut.close();
    }
    public static void main(String[] args) {
        if (args.length != 1) throw new IllegalArgumentException("use - or +");
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("use - or +");
    }
}
