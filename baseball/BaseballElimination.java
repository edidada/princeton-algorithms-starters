import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaseballElimination {
    private final Map<String, Integer> indices = new LinkedHashMap<>();
    private final String[] names;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] games;
    private final Result[] results;
    private static final class Result {
        private final List<String> certificate;
        Result(List<String> certificate) {
            this.certificate = certificate == null ? null : Collections.unmodifiableList(new ArrayList<>(certificate));
        }
    }

    public BaseballElimination(String filename) {
        if (filename == null) throw new IllegalArgumentException("null filename");
        In input = new In(filename);
        int n = input.readInt();
        names = new String[n];
        wins = new int[n];
        losses = new int[n];
        remaining = new int[n];
        games = new int[n][n];
        results = new Result[n];
        for (int i = 0; i < n; i++) {
            names[i] = input.readString();
            indices.put(names[i], i);
            wins[i] = input.readInt();
            losses[i] = input.readInt();
            remaining[i] = input.readInt();
            for (int j = 0; j < n; j++) games[i][j] = input.readInt();
        }
        input.close();
    }
    private int index(String team) {
        if (team == null || !indices.containsKey(team)) throw new IllegalArgumentException("invalid team");
        return indices.get(team);
    }
    public int numberOfTeams() { return names.length; }
    public Iterable<String> teams() { return Collections.unmodifiableSet(indices.keySet()); }
    public int wins(String team) { return wins[index(team)]; }
    public int losses(String team) { return losses[index(team)]; }
    public int remaining(String team) { return remaining[index(team)]; }
    public int against(String team1, String team2) { return games[index(team1)][index(team2)]; }
    public boolean isEliminated(String team) { return result(index(team)).certificate != null; }
    public Iterable<String> certificateOfElimination(String team) { return result(index(team)).certificate; }

    private Result result(int x) {
        if (results[x] == null) results[x] = solve(x);
        return results[x];
    }
    private Result solve(int x) {
        int n = names.length;
        long maximum = (long) wins[x] + remaining[x];
        for (int i = 0; i < n; i++)
            if (i != x && wins[i] > maximum) return new Result(Collections.singletonList(names[i]));

        int gameCount = 0;
        for (int i = 0; i < n; i++) for (int j = i+1; j < n; j++)
            if (i != x && j != x && games[i][j] > 0) gameCount++;
        int source = 0, sink = n + gameCount;
        FlowNetwork network = new FlowNetwork(sink+1);
        int[] teamVertex = new int[n];
        int next = 1;
        for (int i = 0; i < n; i++) if (i != x) {
            teamVertex[i] = next++;
            network.addEdge(new FlowEdge(teamVertex[i], sink, maximum-wins[i]));
        }
        int gameVertex = n;
        long totalGames = 0;
        for (int i = 0; i < n; i++) for (int j = i+1; j < n; j++) {
            if (i == x || j == x || games[i][j] == 0) continue;
            network.addEdge(new FlowEdge(source, gameVertex, games[i][j]));
            network.addEdge(new FlowEdge(gameVertex, teamVertex[i], Double.POSITIVE_INFINITY));
            network.addEdge(new FlowEdge(gameVertex, teamVertex[j], Double.POSITIVE_INFINITY));
            totalGames += games[i][j];
            gameVertex++;
        }
        FordFulkerson flow = new FordFulkerson(network, source, sink);
        if (flow.value() >= totalGames) return new Result(null);
        List<String> certificate = new ArrayList<>();
        for (int i = 0; i < n; i++) if (i != x && flow.inCut(teamVertex[i])) certificate.add(names[i]);
        return new Result(certificate);
    }
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String member : division.certificateOfElimination(team)) StdOut.print(member + " ");
                StdOut.println("}");
            }
            else StdOut.println(team + " is not eliminated");
        }
    }
}
