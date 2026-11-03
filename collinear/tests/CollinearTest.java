import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.nio.file.*;

class CollinearTest {
    static Set<String> strings(LineSegment[] segments) {
        Set<String> result = new TreeSet<>();
        for (LineSegment s : segments) assertTrue(result.add(s.toString()), "duplicate segment");
        return result;
    }
    @Test void pointContracts() {
        Point o = new Point(0,0);
        assertEquals(Double.NEGATIVE_INFINITY, o.slopeTo(new Point(0,0)));
        assertEquals(Double.POSITIVE_INFINITY, o.slopeTo(new Point(0,1)));
        assertEquals(0L, Double.doubleToRawLongBits(o.slopeTo(new Point(-1,0))));
        assertEquals(-0.5, o.slopeTo(new Point(2,-1)));
        assertTrue(new Point(1,2).compareTo(new Point(0,3)) < 0);
        assertTrue(new Point(1,2).compareTo(new Point(2,2)) < 0);
        assertEquals(0, o.slopeOrder().compare(new Point(1,2), new Point(2,4)));
    }
    @Test void validationAndDefensiveCopies() {
        for (Point[] bad : new Point[][]{null, {null}, {new Point(1,1),new Point(1,1)}}) {
            assertThrows(IllegalArgumentException.class, () -> new FastCollinearPoints(bad));
            assertThrows(IllegalArgumentException.class, () -> new BruteCollinearPoints(bad));
        }
        Point[] p = {new Point(3,3),new Point(0,0),new Point(2,2),new Point(1,1)};
        Point first = p[0];
        FastCollinearPoints f = new FastCollinearPoints(p);
        assertSame(first,p[0]);
        p[0] = null;
        f.segments()[0] = null;
        assertEquals(Set.of("(0, 0) -> (3, 3)"),strings(f.segments()));
        assertEquals(0,new FastCollinearPoints(new Point[0]).numberOfSegments());
    }
    @Test void suppliedEightPointFixture() throws Exception {
        try (Scanner s = new Scanner(Path.of("input8.txt"))) {
            Point[] p = new Point[s.nextInt()];
            for (int i=0;i<p.length;i++) p[i]=new Point(s.nextInt(),s.nextInt());
            Set<String> expected=Set.of("(10000, 0) -> (0, 10000)","(3000, 4000) -> (20000, 21000)");
            assertEquals(expected,strings(new FastCollinearPoints(p).segments()));
            assertEquals(expected,strings(new BruteCollinearPoints(p).segments()));
        }
    }
    // Independent exact integer cross-product oracle; does not reuse slopeTo.
    static Set<String> oracle(int[][] xy, Point[] p) {
        Set<String> expected = new TreeSet<>();
        for(int a=0;a<p.length;a++) for(int b=a+1;b<p.length;b++) {
            int count=0; Point min=p[a],max=p[a];
            for(int c=0;c<p.length;c++) {
                long dx=xy[b][0]-xy[a][0],dy=xy[b][1]-xy[a][1];
                if(dx*(xy[c][1]-xy[a][1]) == dy*(xy[c][0]-xy[a][0])) {
                    count++;
                    if(p[c].compareTo(min)<0) min=p[c];
                    if(p[c].compareTo(max)>0) max=p[c];
                }
            }
            if(count>=4) expected.add(min+" -> "+max);
        }
        return expected;
    }
    @Test void randomizedExactGeometryOracle() {
        Random random=new Random(1703);
        for(int trial=0;trial<300;trial++) {
            int n=4+random.nextInt(35); int[][] xy=new int[n][2];Point[] p=new Point[n];
            Set<String> occupied=new HashSet<>();
            for(int i=0;i<n;i++) {
                int x,y;
                do { x=random.nextInt(15); y=random.nextInt(15); } while(!occupied.add(x+":"+y));
                xy[i]=new int[]{x,y};p[i]=new Point(x,y);
            }
            assertEquals(oracle(xy,p), strings(new FastCollinearPoints(p).segments()),"trial "+trial);
        }
    }
    @Test void longLineMaximalityAndBruteFourTuples() {
        Point[] p=new Point[7];for(int i=0;i<7;i++)p[i]=new Point(i,2*i);
        assertEquals(Set.of("(0, 0) -> (6, 12)"),strings(new FastCollinearPoints(p).segments()));
        // Official Brute inputs exclude five or more collinear points; record extended behavior.
        assertEquals(35,new BruteCollinearPoints(p).numberOfSegments());
    }
}
