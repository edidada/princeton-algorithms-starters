import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class CircularSuffixTest {
    static int compare(String s,int a,int b) {
        for(int i=0;i<s.length();i++) {
            int c=Character.compare(s.charAt((a+i)%s.length()),s.charAt((b+i)%s.length()));
            if(c!=0)return c;
        }
        return 0;
    }
    static void check(String s) {
        CircularSuffixArray c=new CircularSuffixArray(s);assertEquals(s.length(),c.length());
        boolean[] seen=new boolean[s.length()];
        for(int i=0;i<c.length();i++) {
            int index=c.index(i);assertTrue(index>=0 && index<s.length());assertFalse(seen[index]);seen[index]=true;
            if(i>0)assertTrue(compare(s,c.index(i-1),index)<=0,"out of order: "+s);
        }
    }
    @Test void officialAndCornerCases() {
        CircularSuffixArray c=new CircularSuffixArray("ABRACADABRA!");int[] expected={11,10,7,0,3,5,8,1,4,6,9,2};
        for(int i=0;i<12;i++)assertEquals(expected[i],c.index(i));
        for(String s:new String[]{"","A","AAAAAA","ABABABAB","BANANA","\u0000\uffff\u1234\u0000"})check(s);
        assertThrows(IllegalArgumentException.class,()->new CircularSuffixArray(null));
        assertThrows(IllegalArgumentException.class,()->c.index(-1));assertThrows(IllegalArgumentException.class,()->c.index(12));
        assertThrows(IllegalArgumentException.class,()->new CircularSuffixArray("").index(0));
    }
    @Test void randomStringsAgainstFullRotationComparison() {
        Random random=new Random(1103);
        for(int trial=0;trial<1000;trial++) {
            int n=random.nextInt(80);StringBuilder s=new StringBuilder();
            for(int i=0;i<n;i++)s.append((char)random.nextInt(trial%2==0?4:256));check(s.toString());
        }
    }
    @Test void largePeriodicInput() {
        int n=300000;CircularSuffixArray c=new CircularSuffixArray("A".repeat(n));assertEquals(n,c.length());
        boolean[] seen=new boolean[n];for(int i=0;i<n;i++){int p=c.index(i);assertTrue(p>=0 && p<n);assertFalse(seen[p]);seen[p]=true;}
    }
}
