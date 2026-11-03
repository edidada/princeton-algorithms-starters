import edu.princeton.cs.algs4.Picture;

public class SeamRender {
    public static void main(String[] args) {
        SeamCarver carver=new SeamCarver(new Picture(args[0]));
        int w=carver.width(),h=carver.height();
        for(int i=0;i<100;i++)carver.removeVerticalSeam(carver.findVerticalSeam());
        for(int i=0;i<30;i++)carver.removeHorizontalSeam(carver.findHorizontalSeam());
        Picture result=carver.picture();result.save("target/carved.png");
        if(result.width()!=w-100 || result.height()!=h-30)throw new AssertionError("wrong dimensions");
        System.out.println("image "+w+"x"+h+" -> "+result.width()+"x"+result.height());
    }
}
