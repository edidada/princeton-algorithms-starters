import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

/** Independent Monte Carlo threshold estimates and 95% confidence interval. */
public final class PercolationStats {
    private final double average;
    private final double deviation;
    private final double margin;

    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) throw new IllegalArgumentException("Positive n and trials required");
        double[] thresholds = new double[trials];
        for (int trial = 0; trial < trials; trial++) {
            Percolation grid = new Percolation(n);
            while (!grid.percolates()) {
                grid.open(StdRandom.uniform(n) + 1, StdRandom.uniform(n) + 1);
            }
            thresholds[trial] = grid.numberOfOpenSites() / ((double) n * n);
        }
        average = StdStats.mean(thresholds);
        deviation = StdStats.stddev(thresholds);
        margin = 1.96 * deviation / Math.sqrt(trials);
    }

    public double mean() { return average; }
    public double stddev() { return deviation; }
    public double confidenceLo() { return average - margin; }
    public double confidenceHi() { return average + margin; }

    public static void main(String[] args) {
        if (args.length != 2) throw new IllegalArgumentException("Usage: PercolationStats n trials");
        PercolationStats stats = new PercolationStats(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        System.out.printf("mean                    = %.16f%n", stats.mean());
        System.out.printf("stddev                  = %.16f%n", stats.stddev());
        System.out.printf("95%% confidence interval = [%.16f, %.16f]%n", stats.confidenceLo(), stats.confidenceHi());
    }
}
