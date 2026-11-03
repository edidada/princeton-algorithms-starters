import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastCollinearPoints {
    private final LineSegment[] result;
    public FastCollinearPoints(Point[] points) {
        Point[] sorted = BruteCollinearPoints.checked(points);
        Point[] bySlope = sorted.clone();
        List<LineSegment> found = new ArrayList<>();
        for (Point origin : sorted) {
            Arrays.sort(bySlope, origin.slopeOrder());
            int start = 1;
            while (start < bySlope.length) {
                int end = start + 1;
                while (end < bySlope.length && Double.compare(origin.slopeTo(bySlope[start]),
                        origin.slopeTo(bySlope[end])) == 0) end++;
                if (end - start >= 3) {
                    Point min = origin, max = origin;
                    for (int i = start; i < end; i++) {
                        if (bySlope[i].compareTo(min) < 0) min = bySlope[i];
                        if (bySlope[i].compareTo(max) > 0) max = bySlope[i];
                    }
                    if (origin.compareTo(min) == 0) found.add(new LineSegment(min, max));
                }
                start = end;
            }
        }
        result = found.toArray(new LineSegment[0]);
    }
    public int numberOfSegments() { return result.length; }
    public LineSegment[] segments() { return result.clone(); }
}
