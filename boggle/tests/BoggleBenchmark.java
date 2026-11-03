import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import java.util.Arrays;

public class BoggleBenchmark {
    public static void main(String[] args) {
        In input=new In("dictionary-yawl.txt");
        BoggleSolver solver=new BoggleSolver(input.readAllStrings());input.close();
        StdRandom.setSeed(20261103);
        BoggleBoard[] boards=new BoggleBoard[2000];for(int i=0;i<boards.length;i++)boards[i]=new BoggleBoard();
        long checksum=0;
        for(int round=0;round<3;round++)for(BoggleBoard board:boards)
            for(String word:solver.getAllValidWords(board))checksum+=word.length();
        double[] rates=new double[5];
        for(int round=0;round<rates.length;round++) {
            long start=System.nanoTime();
            for(BoggleBoard board:boards)for(String word:solver.getAllValidWords(board))checksum+=word.length();
            rates[round]=boards.length/((System.nanoTime()-start)/1e9);
            System.out.printf("round %d: %.1f boards/s%n",round,rates[round]);
        }
        Arrays.sort(rates);
        System.out.printf("median=%.1f boards/s; checksum=%d; dictionary=yawl; board generation excluded%n",rates[2],checksum);
        if(rates[2]<2000)throw new AssertionError("median does not reach thousands of boards/s");
    }
}
