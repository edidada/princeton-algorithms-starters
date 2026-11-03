import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class DrawSmoke {
    public static void main(String[] args) throws Exception {
        StdDraw.setCanvasSize(512,512);
        StdDraw.enableDoubleBuffering();
        KdTree kd=new KdTree();PointSET brute=new PointSET();
        double[][] points={{.7,.2},{.5,.4},{.2,.3},{.4,.7},{.9,.6}};
        for(double[] p:points){kd.insert(new Point2D(p[0],p[1]));brute.insert(new Point2D(p[0],p[1]));}
        kd.draw();StdDraw.show();StdDraw.save("target/kdtree.png");
        BufferedImage image=ImageIO.read(new File("target/kdtree.png"));
        int red=0,blue=0,black=0;
        for(int y=0;y<image.getHeight();y++)for(int x=0;x<image.getWidth();x++) {
            int rgb=image.getRGB(x,y);int r=(rgb>>16)&255,g=(rgb>>8)&255,b=rgb&255;
            if(r>180 && g<80 && b<80)red++;
            if(b>180 && r<80 && g<80)blue++;
            if(r<50 && g<50 && b<50)black++;
        }
        if(red<200 || blue<100 || black<30)throw new AssertionError("missing split lines or points");
        System.out.println("render pixels: red="+red+" blue="+blue+" black="+black);
        StdDraw.clear();brute.draw();StdDraw.show();StdDraw.save("target/pointset.png");
        System.exit(0);
    }
}
