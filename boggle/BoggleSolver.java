import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {
    private static final class Node {
        private final Node[] next = new Node[26];
        private String word;
    }
    private final Node root = new Node();

    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) throw new IllegalArgumentException("null dictionary");
        for (String word : dictionary) {
            if (word == null) throw new IllegalArgumentException("null word");
            Node node = root;
            for (int i = 0; i < word.length(); i++) {
                int letter = word.charAt(i)-'A';
                if (letter < 0 || letter >= 26) throw new IllegalArgumentException("uppercase A-Z required");
                if (node.next[letter] == null) node.next[letter] = new Node();
                node = node.next[letter];
            }
            if (word.length() >= 3) node.word = word;
        }
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) throw new IllegalArgumentException("null board");
        int rows = board.rows(), cols = board.cols(), count = rows*cols;
        char[] letters = new char[count];
        int[][] neighbors = new int[count][];
        for (int i = 0; i < count; i++) {
            int row = i/cols, col = i%cols;
            letters[i] = board.getLetter(row, col);
            int[] adjacent = new int[8];
            int size = 0;
            for (int dr = -1; dr <= 1; dr++) for (int dc = -1; dc <= 1; dc++) {
                int r = row+dr, c = col+dc;
                if ((dr != 0 || dc != 0) && r >= 0 && r < rows && c >= 0 && c < cols)
                    adjacent[size++] = r*cols+c;
            }
            neighbors[i] = java.util.Arrays.copyOf(adjacent, size);
        }
        Set<String> words = new HashSet<>();
        boolean[] used = new boolean[count];
        for (int i = 0; i < count; i++) search(i, root, letters, neighbors, used, words);
        return words;
    }

    private static void search(int cell, Node prefix, char[] letters, int[][] neighbors,
                               boolean[] used, Set<String> words) {
        if (used[cell]) return;
        Node node = prefix.next[letters[cell]-'A'];
        if (node == null) return;
        if (letters[cell] == 'Q') {
            node = node.next['U'-'A'];
            if (node == null) return;
        }
        if (node.word != null) words.add(node.word);
        used[cell] = true;
        for (int next : neighbors[cell]) search(next, node, letters, neighbors, used, words);
        used[cell] = false;
    }

    public int scoreOf(String word) {
        if (word == null) throw new IllegalArgumentException("null word");
        Node node = root;
        for (int i = 0; i < word.length(); i++) {
            int letter = word.charAt(i)-'A';
            if (letter < 0 || letter >= 26 || node.next[letter] == null) return 0;
            node = node.next[letter];
        }
        if (node.word == null) return 0;
        int n = word.length();
        if (n <= 4) return 1;
        if (n == 5) return 2;
        if (n == 6) return 3;
        if (n == 7) return 5;
        return 11;
    }

    public static void main(String[] args) {
        In input = new In(args[0]);
        BoggleSolver solver = new BoggleSolver(input.readAllStrings());
        input.close();
        int score = 0;
        for (String word : solver.getAllValidWords(new BoggleBoard(args[1]))) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
