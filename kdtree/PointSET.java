import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class PointSET {
    private final TreeSet<Point2D> points = new TreeSet<>();
    public PointSET() { }
    public boolean isEmpty() { return points.isEmpty(); }
    public int size() { return points.size(); }
    private static void require(Object value) {
        if (value == null) throw new IllegalArgumentException("null argument");
    }
    public void insert(Point2D p) { require(p); points.add(p); }
    public boolean contains(Point2D p) { require(p); return points.contains(p); }
    public void draw() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D point : points) point.draw();
    }
    public Iterable<Point2D> range(RectHV rect) {
        require(rect);
        List<Point2D> found = new ArrayList<>();
        for (Point2D point : points) if (rect.contains(point)) found.add(point);
        return found;
    }
    public Point2D nearest(Point2D p) {
        require(p);
        Point2D best = null;
        double distance = Double.POSITIVE_INFINITY;
        for (Point2D point : points) {
            double candidate = p.distanceSquaredTo(point);
            if (best == null || candidate < distance) {
                best = point;
                distance = candidate;
            }
        }
        return best;
    }
}
