import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private int[][] pixels;
    private int width;
    private int height;
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("null picture");
        width = picture.width();
        height = picture.height();
        pixels = new int[height][width];
        for (int y = 0; y < height; y++) for (int x = 0; x < width; x++)
            pixels[y][x] = picture.getRGB(x, y);
    }
    public Picture picture() {
        Picture copy = new Picture(width, height);
        for (int y = 0; y < height; y++) for (int x = 0; x < width; x++)
            copy.setRGB(x, y, pixels[y][x]);
        return copy;
    }
    public int width() { return width; }
    public int height() { return height; }
    private static int gradient(int a, int b) {
        int red = ((a >>> 16)&255)-((b >>> 16)&255);
        int green = ((a >>> 8)&255)-((b >>> 8)&255);
        int blue = (a&255)-(b&255);
        return red*red + green*green + blue*blue;
    }
    public double energy(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            throw new IllegalArgumentException("invalid pixel");
        if (x == 0 || y == 0 || x == width-1 || y == height-1) return 1000;
        return Math.sqrt(gradient(pixels[y][x-1], pixels[y][x+1])
                       + gradient(pixels[y-1][x], pixels[y+1][x]));
    }
    public int[] findVerticalSeam() { return find(true); }
    public int[] findHorizontalSeam() { return find(false); }
    private int[] find(boolean vertical) {
        int steps = vertical ? height : width;
        int across = vertical ? width : height;
        double[] previous = new double[across];
        int[][] parent = new int[steps][across];
        for (int i = 0; i < across; i++) previous[i] = 1000;
        for (int step = 1; step < steps; step++) {
            double[] next = new double[across];
            for (int i = 0; i < across; i++) {
                int best = i;
                if (i > 0 && previous[i-1] < previous[best]) best = i-1;
                if (i+1 < across && previous[i+1] < previous[best]) best = i+1;
                next[i] = previous[best] + energy(vertical ? i : step, vertical ? step : i);
                parent[step][i] = best;
            }
            previous = next;
        }
        int best = 0;
        for (int i = 1; i < across; i++) if (previous[i] < previous[best]) best = i;
        int[] seam = new int[steps];
        for (int step = steps-1; step >= 0; step--) {
            seam[step] = best;
            best = parent[step][best];
        }
        return seam;
    }
    private void validate(int[] seam, boolean vertical) {
        int length = vertical ? height : width;
        int bound = vertical ? width : height;
        if (seam == null || bound <= 1 || seam.length != length)
            throw new IllegalArgumentException("invalid seam size");
        for (int i = 0; i < length; i++)
            if (seam[i] < 0 || seam[i] >= bound || (i > 0 && Math.abs(seam[i]-seam[i-1]) > 1))
                throw new IllegalArgumentException("invalid seam path");
    }
    public void removeVerticalSeam(int[] seam) {
        validate(seam, true);
        int[][] next = new int[height][width-1];
        for (int y = 0; y < height; y++) {
            System.arraycopy(pixels[y], 0, next[y], 0, seam[y]);
            System.arraycopy(pixels[y], seam[y]+1, next[y], seam[y], width-seam[y]-1);
        }
        pixels = next;
        width--;
    }
    public void removeHorizontalSeam(int[] seam) {
        validate(seam, false);
        int[][] next = new int[height-1][width];
        for (int y = 0; y < height-1; y++) for (int x = 0; x < width; x++)
            next[y][x] = pixels[y < seam[x] ? y : y+1][x];
        pixels = next;
        height--;
    }
}
