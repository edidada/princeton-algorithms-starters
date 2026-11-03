import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayDeque;
import java.util.Random;
import org.junit.jupiter.api.Test;

class PercolationTest {
    @Test void singleCellAndBackwash() {
        Percolation one = new Percolation(1);
        assertFalse(one.percolates());
        one.open(1, 1);
        one.open(1, 1);
        assertTrue(one.percolates());
        assertTrue(one.isFull(1, 1));
        assertEquals(1, one.numberOfOpenSites());
        Percolation grid = new Percolation(3);
        grid.open(1, 1); grid.open(2, 1); grid.open(3, 1); grid.open(3, 3);
        assertTrue(grid.percolates());
        assertFalse(grid.isFull(3, 3), "No backwash through virtual bottom");
        assertFalse(grid.isFull(1, 2), "Blocked top site is not full");
    }

    @Test void allOperationsAgainstFloodFill() {
        Random random = new Random(10101);
        for (int trial = 0; trial < 200; trial++) {
            int n = 1 + random.nextInt(12);
            Percolation grid = new Percolation(n);
            boolean[][] open = new boolean[n][n];
            int count = 0;
            for (int step = 0; step < n * n * 2; step++) {
                int row = random.nextInt(n), col = random.nextInt(n);
                grid.open(row + 1, col + 1);
                if (!open[row][col]) count++;
                open[row][col] = true;
                boolean[][] full = flood(open);
                boolean percolates = false;
                for (int r = 0; r < n; r++) {
                    for (int c = 0; c < n; c++) {
                        assertEquals(open[r][c], grid.isOpen(r + 1, c + 1));
                        assertEquals(full[r][c], grid.isFull(r + 1, c + 1));
                    }
                    percolates |= full[n - 1][r];
                }
                assertEquals(percolates, grid.percolates());
                assertEquals(count, grid.numberOfOpenSites());
            }
        }
    }

    private boolean[][] flood(boolean[][] open) {
        int n = open.length;
        boolean[][] full = new boolean[n][n];
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int c = 0; c < n; c++) if (open[0][c]) {
            full[0][c] = true; queue.add(c);
        }
        while (!queue.isEmpty()) {
            int cell = queue.remove(), row = cell / n, col = cell % n;
            for (int[] delta : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                int r = row + delta[0], c = col + delta[1];
                if (r >= 0 && r < n && c >= 0 && c < n && open[r][c] && !full[r][c]) {
                    full[r][c] = true; queue.add(r * n + c);
                }
            }
        }
        return full;
    }

    @Test void invalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Percolation(0));
        assertThrows(IllegalArgumentException.class, () -> new Percolation(-1));
        Percolation grid = new Percolation(3);
        for (int[] site : new int[][]{{0, 1}, {1, 0}, {4, 1}, {1, 4}}) {
            assertThrows(IllegalArgumentException.class, () -> grid.open(site[0], site[1]));
            assertThrows(IllegalArgumentException.class, () -> grid.isOpen(site[0], site[1]));
            assertThrows(IllegalArgumentException.class, () -> grid.isFull(site[0], site[1]));
        }
        assertThrows(IllegalArgumentException.class, () -> new PercolationStats(1, 0));
        assertThrows(IllegalArgumentException.class, () -> new PercolationStats(0, 1));
    }

    @Test void statisticsAndConfidenceInterval() {
        PercolationStats exact = new PercolationStats(1, 5);
        assertEquals(1.0, exact.mean());
        assertEquals(0.0, exact.stddev());
        assertEquals(1.0, exact.confidenceLo());
        assertEquals(1.0, exact.confidenceHi());
        assertTrue(Double.isNaN(new PercolationStats(1, 1).stddev()));
        PercolationStats estimate = new PercolationStats(20, 100);
        assertTrue(estimate.mean() > 0 && estimate.mean() <= 1);
        assertTrue(estimate.stddev() >= 0);
        assertEquals(1.96 * estimate.stddev() / 10,
                     estimate.confidenceHi() - estimate.mean(), 1e-12);
        assertEquals(estimate.mean() - estimate.confidenceLo(),
                     estimate.confidenceHi() - estimate.mean(), 1e-12);
    }
}
