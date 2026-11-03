import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solver {
    private final List<Board> path;

    private static final class Node implements Comparable<Node> {
        private final Board board;
        private final int moves;
        private final int heuristic;
        private final int priority;
        private final Node previous;
        Node(Board board, int moves, Node previous) {
            this.board = board;
            this.moves = moves;
            this.previous = previous;
            heuristic = board.manhattan();
            priority = moves + heuristic;
        }
        public int compareTo(Node other) {
            int c = Integer.compare(priority, other.priority);
            return c != 0 ? c : Integer.compare(heuristic, other.heuristic);
        }
    }

    private static final class Search {
        private final MinPQ<Node> queue = new MinPQ<>();
        private final Map<Board, Integer> best = new HashMap<>();
        Search(Board board) {
            queue.insert(new Node(board, 0, null));
            best.put(board, 0);
        }
        Node advance() {
            Node current;
            do {
                current = queue.delMin();
            } while (current.moves != best.get(current.board));
            if (current.board.isGoal()) return current;
            for (Board next : current.board.neighbors()) {
                if (current.previous != null && next.equals(current.previous.board)) continue;
                int moves = current.moves+1;
                if (moves < best.getOrDefault(next, Integer.MAX_VALUE)) {
                    best.put(next, moves);
                    queue.insert(new Node(next, moves, current));
                }
            }
            return null;
        }
    }

    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("null board");
        Search original = new Search(initial);
        Search twin = new Search(initial.twin());
        Node goal;
        while (true) {
            goal = original.advance();
            if (goal != null || twin.advance() != null) break;
        }
        if (goal == null) path = null;
        else {
            List<Board> reverse = new ArrayList<>();
            for (Node node = goal; node != null; node = node.previous) reverse.add(node.board);
            Collections.reverse(reverse);
            path = Collections.unmodifiableList(reverse);
        }
    }

    public boolean isSolvable() { return path != null; }
    public int moves() { return path == null ? -1 : path.size()-1; }
    public Iterable<Board> solution() { return path; }

    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) tiles[i][j] = in.readInt();
        Solver solver = new Solver(new Board(tiles));
        if (!solver.isSolvable()) StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) StdOut.println(board);
        }
    }
}
