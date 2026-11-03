import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import edu.princeton.cs.algs4.Picture;
import java.awt.Color;
import java.util.*;

class SeamTest {
    static Picture random(int w,int h,Random r) {
        Picture p=new Picture(w,h);
        for(int y=0;y<h;y++)for(int x=0;x<w;x++)p.set(x,y,new Color(r.nextInt(256),r.nextInt(256),r.nextInt(256)));
        return p;
    }
    static double energy(Picture p,int x,int y) {
        if(x==0 || y==0 || x==p.width()-1 || y==p.height()-1)return 1000;
        Color left=p.get(x-1,y),right=p.get(x+1,y),up=p.get(x,y-1),down=p.get(x,y+1);
        int[] differences={left.getRed()-right.getRed(),left.getGreen()-right.getGreen(),left.getBlue()-right.getBlue(),up.getRed()-down.getRed(),up.getGreen()-down.getGreen(),up.getBlue()-down.getBlue()};
        int sum=0;for(int value:differences)sum+=value*value;return Math.sqrt(sum);
    }
    static double exhaustive(Picture p,boolean vertical,int step,int previous) {
        int steps=vertical?p.height():p.width(),across=vertical?p.width():p.height();
        if(step==steps)return 0;
        double best=Double.POSITIVE_INFINITY;
        for(int i=0;i<across;i++)if(step==0 || Math.abs(i-previous)<=1)
            best=Math.min(best,energy(p,vertical?i:step,vertical?step:i)+exhaustive(p,vertical,step+1,i));
        return best;
    }
    static void checkSeam(Picture p,SeamCarver carver,boolean vertical) {
        int[] seam=vertical?carver.findVerticalSeam():carver.findHorizontalSeam();
        assertEquals(vertical?p.height():p.width(),seam.length);double total=0;
        for(int i=0;i<seam.length;i++) {
            assertTrue(seam[i]>=0 && seam[i]<(vertical?p.width():p.height()));
            if(i>0)assertTrue(Math.abs(seam[i]-seam[i-1])<=1);
            total+=energy(p,vertical?seam[i]:i,vertical?i:seam[i]);
        }
        assertEquals(exhaustive(p,vertical,0,0),total,1e-8);
    }
    static void pixelsEqual(Picture expected,Picture actual) {
        assertEquals(expected.width(),actual.width());assertEquals(expected.height(),actual.height());
        for(int y=0;y<expected.height();y++)for(int x=0;x<expected.width();x++)assertEquals(expected.getRGB(x,y),actual.getRGB(x,y));
    }
    @Test void officialEnergyExample() {
        SeamCarver s=new SeamCarver(new Picture("3x4.png"));
        assertEquals(Math.sqrt(52225),s.energy(1,1),1e-10);assertEquals(Math.sqrt(52024),s.energy(1,2),1e-10);
        assertEquals(1000,s.energy(0,0));
        Picture p=new Picture("6x5.png");SeamCarver c=new SeamCarver(p);checkSeam(p,c,true);checkSeam(p,c,false);
    }
    @Test void randomizedAllPathsOracle() {
        Random r=new Random(20261103);
        for(int trial=0;trial<120;trial++) {
            Picture p=random(1+r.nextInt(6),1+r.nextInt(6),r);SeamCarver s=new SeamCarver(p);
            for(int y=0;y<p.height();y++)for(int x=0;x<p.width();x++)assertEquals(energy(p,x,y),s.energy(x,y),1e-10);
            checkSeam(p,s,true);checkSeam(p,s,false);pixelsEqual(p,s.picture());
        }
    }
    @Test void repeatedMixedRemovalsPreservePixelsAndEnergy() {
        Random r=new Random(148);
        for(int trial=0;trial<30;trial++) {
            Picture p=random(3+r.nextInt(4),3+r.nextInt(4),r);SeamCarver s=new SeamCarver(p);
            while(p.width()>1 || p.height()>1) {
                boolean vertical=p.height()==1 || (p.width()>1 && r.nextBoolean());
                int[] seam=vertical?s.findVerticalSeam():s.findHorizontalSeam();
                Picture next=new Picture(p.width()-(vertical?1:0),p.height()-(vertical?0:1));
                if(vertical) {
                    for(int y=0;y<p.height();y++) { int write=0;for(int x=0;x<p.width();x++)if(x!=seam[y])next.setRGB(write++,y,p.getRGB(x,y)); }
                    s.removeVerticalSeam(seam);
                }
                else {
                    for(int x=0;x<p.width();x++) { int write=0;for(int y=0;y<p.height();y++)if(y!=seam[x])next.setRGB(x,write++,p.getRGB(x,y)); }
                    s.removeHorizontalSeam(seam);
                }
                p=next;pixelsEqual(p,s.picture());
                for(int y=0;y<p.height();y++)for(int x=0;x<p.width();x++)assertEquals(energy(p,x,y),s.energy(x,y),1e-10);
                checkSeam(p,s,true);checkSeam(p,s,false);
            }
            assertThrows(IllegalArgumentException.class,()->s.removeVerticalSeam(new int[]{0}));
            assertThrows(IllegalArgumentException.class,()->s.removeHorizontalSeam(new int[]{0}));
        }
    }
    @Test void invalidArgumentsDoNotMutate() {
        assertThrows(IllegalArgumentException.class,()->new SeamCarver(null));
        Picture p=random(4,4,new Random(4));SeamCarver s=new SeamCarver(p);
        for(int[] seam:new int[][]{null,{}, {0,0}, {-1,0,0,0}, {0,0,0,4}, {0,2,2,2}}) {
            assertThrows(IllegalArgumentException.class,()->s.removeVerticalSeam(seam));
            assertThrows(IllegalArgumentException.class,()->s.removeHorizontalSeam(seam));pixelsEqual(p,s.picture());
        }
        for(int[] xy:new int[][]{{-1,0},{0,-1},{4,0},{0,4}})assertThrows(IllegalArgumentException.class,()->s.energy(xy[0],xy[1]));
    }
    @Test void defensivePictureCopiesAndNarrowImages() {
        Picture original=random(3,3,new Random(7)),expected=new Picture(original);SeamCarver s=new SeamCarver(original);
        original.setRGB(0,0,0);s.picture().setRGB(1,1,0);pixelsEqual(expected,s.picture());
        int[] seam=s.findVerticalSeam();seam[0]=999;pixelsEqual(expected,s.picture());
        for(int[] size:new int[][]{{1,1},{1,6},{6,1},{2,2}}) {
            Picture p=random(size[0],size[1],new Random(1));SeamCarver c=new SeamCarver(p);
            checkSeam(p,c,true);checkSeam(p,c,false);
        }
    }
}
