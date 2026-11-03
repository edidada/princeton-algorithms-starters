import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints {
    private final LineSegment[] result;
    static Point[] checked(Point[] points) {
        if (points == null) throw new IllegalArgumentException("null array");
        Point[] copy = points.clone();
        for (Point p : copy) if (p == null) throw new IllegalArgumentException("null point");
        Arrays.sort(copy);
        for (int i = 1; i < copy.length; i++)
            if (copy[i-1].compareTo(copy[i]) == 0) throw new IllegalArgumentException("duplicate point");
        return copy;
    }
    public BruteCollinearPoints(Point[] points) {
        Point[] p = checked(points);
        List<LineSegment> found = new ArrayList<>();
        for (int a = 0; a < p.length; a++)
            for (int b = a+1; b < p.length; b++)
                for (int c = b+1; c < p.length; c++)
                    for (int d = c+1; d < p.length; d++)
                        if (Double.compare(p[a].slopeTo(p[b]), p[a].slopeTo(p[c])) == 0
                                && Double.compare(p[a].slopeTo(p[b]), p[a].slopeTo(p[d])) == 0)
                            found.add(new LineSegment(p[a], p[d]));
        result = found.toArray(new LineSegment[0]);
    }
    public int numberOfSegments() { return result.length; }
    public LineSegment[] segments() { return result.clone(); }
}
