import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import java.util.*;
import java.nio.file.*;

class KdTreeTest {
    static Set<Point2D> set(Iterable<Point2D> points) {
        Set<Point2D> result=new HashSet<>();
        for(Point2D p:points)assertTrue(result.add(p),"duplicate result");
        return result;
    }
    static void query(KdTree kd,PointSET brute,List<Point2D> points,Point2D q,RectHV rect) {
        Set<Point2D> expected=new HashSet<>();double distance=Double.POSITIVE_INFINITY;
        for(Point2D p:points) {
            if(p.x()>=rect.xmin() && p.x()<=rect.xmax() && p.y()>=rect.ymin() && p.y()<=rect.ymax())expected.add(p);
            double dx=p.x()-q.x(),dy=p.y()-q.y();distance=Math.min(distance,dx*dx+dy*dy);
        }
        assertEquals(expected,set(kd.range(rect)));assertEquals(expected,set(brute.range(rect)));
        Point2D a=kd.nearest(q),b=brute.nearest(q);
        assertNotNull(a);assertTrue(points.contains(a));assertTrue(points.contains(b));
        assertEquals(distance,a.distanceSquaredTo(q),1e-14);assertEquals(distance,b.distanceSquaredTo(q),1e-14);
    }
    @Test void emptyNullAndDuplicates() {
        KdTree kd=new KdTree();PointSET brute=new PointSET();Point2D p=new Point2D(.5,.5);
        assertTrue(kd.isEmpty());assertTrue(brute.isEmpty());assertEquals(0,kd.size());
        assertNull(kd.nearest(p));assertNull(brute.nearest(p));
        assertTrue(set(kd.range(new RectHV(0,0,1,1))).isEmpty());
        assertFalse(kd.contains(p));assertFalse(brute.contains(p));
        assertThrows(IllegalArgumentException.class,()->kd.insert(null));
        assertThrows(IllegalArgumentException.class,()->kd.contains(null));
        assertThrows(IllegalArgumentException.class,()->kd.range(null));
        assertThrows(IllegalArgumentException.class,()->kd.nearest(null));
        assertThrows(IllegalArgumentException.class,()->brute.insert(null));
        assertThrows(IllegalArgumentException.class,()->brute.contains(null));
        assertThrows(IllegalArgumentException.class,()->brute.range(null));
        assertThrows(IllegalArgumentException.class,()->brute.nearest(null));
        for(int i=0;i<10;i++){kd.insert(p);brute.insert(p);}
        assertEquals(1,kd.size());assertEquals(1,brute.size());assertFalse(kd.isEmpty());
        assertTrue(kd.contains(new Point2D(.5,.5)));assertFalse(kd.contains(new Point2D(.5,.6)));
        assertEquals(p,kd.nearest(new Point2D(20,-15)));
    }
    @Test void gridBoundariesAndSplitCoordinateTies() {
        KdTree kd=new KdTree();PointSET brute=new PointSET();List<Point2D> points=new ArrayList<>();
        for(int x=0;x<=10;x++)for(int y=0;y<=10;y++) {
            Point2D p=new Point2D(x/10.0,y/10.0);points.add(p);kd.insert(p);brute.insert(p);
        }
        for(Point2D p:points) {assertTrue(kd.contains(p));assertEquals(p,kd.nearest(p));}
        for(double x:new double[]{0,.5,1})for(double y:new double[]{0,.5,1}) {
            query(kd,brute,points,new Point2D(x,y),new RectHV(x,0,x,1));
            query(kd,brute,points,new Point2D(x,y),new RectHV(0,y,1,y));
            query(kd,brute,points,new Point2D(x,y),new RectHV(x,y,x,y));
        }
        query(kd,brute,points,new Point2D(-1,2),new RectHV(-2,-2,2,2));
        query(kd,brute,points,new Point2D(2,-1),new RectHV(2,2,3,3));
    }
    @Test void randomizedIndependentGeometry() {
        Random random=new Random(20261103);
        for(int trial=0;trial<12;trial++) {
            KdTree kd=new KdTree();PointSET brute=new PointSET();List<Point2D> points=new ArrayList<>();
            for(int i=0;i<800;i++) {
                Point2D p=new Point2D(random.nextDouble(),random.nextDouble());
                points.add(p);kd.insert(p);brute.insert(p);
            }
            assertEquals(800,kd.size());assertEquals(800,brute.size());
            for(int i=0;i<150;i++) {
                double a=random.nextDouble()*2-.5,b=random.nextDouble()*2-.5;
                double c=random.nextDouble()*2-.5,d=random.nextDouble()*2-.5;
                query(kd,brute,points,new Point2D(a,b),new RectHV(Math.min(a,c),Math.min(b,d),Math.max(a,c),Math.max(b,d)));
            }
        }
    }
    @Test void skewedTreeDoesNotUseCallStack() {
        KdTree kd=new KdTree();
        for(int i=0;i<8000;i++)kd.insert(new Point2D(i/8000.0,i/8000.0));
        assertEquals(8000,kd.size());assertTrue(kd.contains(new Point2D(7999/8000.0,7999/8000.0)));
        assertEquals(8000,set(kd.range(new RectHV(0,0,1,1))).size());
        assertEquals(new Point2D(.5,.5),kd.nearest(new Point2D(.5,.5)));
    }
    @Test void officialHundredThousandPoints() throws Exception {
        KdTree kd=new KdTree();PointSET brute=new PointSET();List<Point2D> points=new ArrayList<>();
        try(Scanner s=new Scanner(Path.of("input100K.txt"))) {
            while(s.hasNextDouble()) {
                Point2D p=new Point2D(s.nextDouble(),s.nextDouble());points.add(p);kd.insert(p);brute.insert(p);
            }
        }
        assertEquals(100000,points.size());assertEquals(brute.size(),kd.size());
        Random r=new Random(498);
        for(int i=0;i<32;i++) {
            double x=r.nextDouble(),y=r.nextDouble();
            query(kd,brute,points,new Point2D(x,y),new RectHV(x,y,Math.min(1,x+.01),Math.min(1,y+.01)));
        }
    }
}
