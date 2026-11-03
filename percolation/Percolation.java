import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/** n-by-n site percolation, with a second union-find to prevent backwash. */
public final class Percolation {
    private final int size;
    private final boolean[] open;
    private final WeightedQuickUnionUF connectivity;
    private final WeightedQuickUnionUF fullness;
    private final int bottom;
    private int opened;

    public Percolation(int n) {
        if (n <= 0 || (long) n * n > Integer.MAX_VALUE - 2) {
            throw new IllegalArgumentException("Invalid grid size");
        }
        size = n;
        bottom = n * n + 1;
        open = new boolean[n * n + 1];
        connectivity = new WeightedQuickUnionUF(n * n + 2);
        fullness = new WeightedQuickUnionUF(n * n + 1);
    }

    private int index(int row, int col) {
        if (row < 1 || row > size || col < 1 || col > size) {
            throw new IllegalArgumentException("Site outside the grid");
        }
        return (row - 1) * size + col;
    }

    public void open(int row, int col) {
        int site = index(row, col);
        if (open[site]) return;
        open[site] = true;
        opened++;
        if (row == 1) {
            connectivity.union(0, site);
            fullness.union(0, site);
        }
        if (row == size) connectivity.union(bottom, site);
        if (row > 1) connect(site, site - size);
        if (row < size) connect(site, site + size);
        if (col > 1) connect(site, site - 1);
        if (col < size) connect(site, site + 1);
    }

    private void connect(int site, int neighbor) {
        if (open[neighbor]) {
            connectivity.union(site, neighbor);
            fullness.union(site, neighbor);
        }
    }

    public boolean isOpen(int row, int col) {
        return open[index(row, col)];
    }

    public boolean isFull(int row, int col) {
        int site = index(row, col);
        return open[site] && fullness.find(site) == fullness.find(0);
    }

    public int numberOfOpenSites() {
        return opened;
    }

    public boolean percolates() {
        return connectivity.find(0) == connectivity.find(bottom);
    }
}
