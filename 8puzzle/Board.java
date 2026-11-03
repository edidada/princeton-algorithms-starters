import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
    private final int n;
    private final int[] tiles;
    private final int blank;
    private final int hamming;
    private final int manhattan;
    private final int hash;

    public Board(int[][] tiles) {
        this(flatten(tiles), tiles.length);
    }

    private static int[] flatten(int[][] input) {
        if (input == null || input.length < 2 || input.length >= 128)
            throw new IllegalArgumentException("dimension must be in [2, 128)");
        int n = input.length;
        int[] copy = new int[n*n];
        boolean[] seen = new boolean[n*n];
        for (int row = 0; row < n; row++) {
            if (input[row] == null || input[row].length != n)
                throw new IllegalArgumentException("square board required");
            for (int col = 0; col < n; col++) {
                int v = input[row][col];
                if (v < 0 || v >= n*n || seen[v])
                    throw new IllegalArgumentException("tiles must form a permutation");
                seen[v] = true;
                copy[row*n+col] = v;
            }
        }
        return copy;
    }

    // The private constructor takes exclusive ownership of a fresh array.
    private Board(int[] owned, int size) {
        n = size;
        tiles = owned;
        int z = -1, h = 0, m = 0;
        for (int i = 0; i < tiles.length; i++) {
            int value = tiles[i];
            if (value == 0) z = i;
            else {
                if (value != i+1) h++;
                m += Math.abs(i/n - (value-1)/n) + Math.abs(i%n - (value-1)%n);
            }
        }
        blank = z;
        hamming = h;
        manhattan = m;
        hash = 31*n + Arrays.hashCode(tiles);
    }

    public int dimension() { return n; }
    public int hamming() { return hamming; }
    public int manhattan() { return manhattan; }
    public boolean isGoal() { return hamming == 0; }

    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || other.getClass() != getClass()) return false;
        Board that = (Board) other;
        return n == that.n && Arrays.equals(tiles, that.tiles);
    }

    public int hashCode() { return hash; }

    private Board exchanged(int a, int b) {
        int[] copy = tiles.clone();
        int value = copy[a]; copy[a] = copy[b]; copy[b] = value;
        return new Board(copy, n);
    }

    public Iterable<Board> neighbors() {
        List<Board> result = new ArrayList<>(4);
        if (blank/n > 0) result.add(exchanged(blank, blank-n));
        if (blank/n+1 < n) result.add(exchanged(blank, blank+n));
        if (blank%n > 0) result.add(exchanged(blank, blank-1));
        if (blank%n+1 < n) result.add(exchanged(blank, blank+1));
        return result;
    }

    public Board twin() {
        int a = tiles[0] == 0 ? 1 : 0;
        int b = a+1;
        if (tiles[b] == 0) b++;
        return exchanged(a, b);
    }

    public String toString() {
        StringBuilder text = new StringBuilder().append(n).append('\n');
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] < 10) text.append(' ');
            text.append(tiles[i]).append(' ');
            if ((i+1)%n == 0) text.append('\n');
        }
        return text.toString();
    }

    public static void main(String[] args) {
        Board board = new Board(new int[][]{{1, 2}, {0, 3}});
        System.out.println(board);
        System.out.println(board.dimension()+" "+board.hamming()+" "+board.manhattan());
        System.out.println(board.isGoal()+" "+board.equals(board.twin()));
        for (Board neighbor : board.neighbors()) System.out.println(neighbor);
    }
}
