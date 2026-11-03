import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class KdTree {
    private static final class Node {
        private final Point2D point;
        private final RectHV rect;
        private final boolean vertical;
        private Node lower;
        private Node upper;
        Node(Point2D point, RectHV rect, boolean vertical) {
            this.point = point;
            this.rect = rect;
            this.vertical = vertical;
        }
        boolean goesLower(Point2D p) {
            return vertical ? p.x() < point.x() : p.y() < point.y();
        }
        RectHV childRect(boolean low) {
            if (vertical)
                return low ? new RectHV(rect.xmin(), rect.ymin(), point.x(), rect.ymax())
                           : new RectHV(point.x(), rect.ymin(), rect.xmax(), rect.ymax());
            return low ? new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), point.y())
                       : new RectHV(rect.xmin(), point.y(), rect.xmax(), rect.ymax());
        }
    }
    private Node root;
    private int size;
    public KdTree() { }
    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }
    private static void require(Object value) {
        if (value == null) throw new IllegalArgumentException("null argument");
    }
    public void insert(Point2D p) {
        require(p);
        if (root == null) {
            root = new Node(p, new RectHV(0, 0, 1, 1), true);
            size = 1;
            return;
        }
        Node node = root;
        while (true) {
            if (node.point.equals(p)) return;
            boolean low = node.goesLower(p);
            Node next = low ? node.lower : node.upper;
            if (next == null) {
                next = new Node(p, node.childRect(low), !node.vertical);
                if (low) node.lower = next;
                else node.upper = next;
                size++;
                return;
            }
            node = next;
        }
    }
    public boolean contains(Point2D p) {
        require(p);
        Node node = root;
        while (node != null) {
            if (node.point.equals(p)) return true;
            node = node.goesLower(p) ? node.lower : node.upper;
        }
        return false;
    }
    public Iterable<Point2D> range(RectHV rect) {
        require(rect);
        List<Point2D> found = new ArrayList<>();
        ArrayDeque<Node> pending = new ArrayDeque<>();
        if (root != null) pending.push(root);
        while (!pending.isEmpty()) {
            Node node = pending.pop();
            if (!node.rect.intersects(rect)) continue;
            if (rect.contains(node.point)) found.add(node.point);
            if (node.lower != null) pending.push(node.lower);
            if (node.upper != null) pending.push(node.upper);
        }
        return found;
    }
    public Point2D nearest(Point2D p) {
        require(p);
        if (root == null) return null;
        Point2D best = root.point;
        double distance = best.distanceSquaredTo(p);
        ArrayDeque<Node> pending = new ArrayDeque<>();
        pending.push(root);
        while (!pending.isEmpty()) {
            Node node = pending.pop();
            if (node.rect.distanceSquaredTo(p) >= distance) continue;
            double candidate = node.point.distanceSquaredTo(p);
            if (candidate < distance) {
                best = node.point;
                distance = candidate;
            }
            boolean low = node.goesLower(p);
            Node near = low ? node.lower : node.upper;
            Node far = low ? node.upper : node.lower;
            // LIFO: visit the near side first, then recheck far-side pruning.
            if (far != null) pending.push(far);
            if (near != null) pending.push(near);
        }
        return best;
    }
    public void draw() {
        ArrayDeque<Node> pending = new ArrayDeque<>();
        List<Point2D> points = new ArrayList<>();
        if (root != null) pending.push(root);
        while (!pending.isEmpty()) {
            Node node = pending.pop();
            StdDraw.setPenRadius();
            if (node.vertical) {
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(node.point.x(), node.rect.ymin(), node.point.x(), node.rect.ymax());
            }
            else {
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.line(node.rect.xmin(), node.point.y(), node.rect.xmax(), node.point.y());
            }
            points.add(node.point);
            if (node.lower != null) pending.push(node.lower);
            if (node.upper != null) pending.push(node.upper);
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D point : points) point.draw();
    }
}
