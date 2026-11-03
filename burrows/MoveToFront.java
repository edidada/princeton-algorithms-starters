import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static void run(boolean encode) {
        char[] alphabet = new char[256];
        for (int i = 0; i < 256; i++) alphabet[i] = (char) i;
        while (!BinaryStdIn.isEmpty()) {
            int input = BinaryStdIn.readChar(), index = input;
            if (encode) {
                index = 0;
                while (alphabet[index] != input) index++;
                BinaryStdOut.write(index, 8);
            }
            else BinaryStdOut.write(alphabet[index], 8);
            char letter = alphabet[index];
            System.arraycopy(alphabet, 0, alphabet, 1, index);
            alphabet[0] = letter;
        }
        BinaryStdOut.close();
    }
    public static void encode() { run(true); }
    public static void decode() { run(false); }
    public static void main(String[] args) {
        if (args.length != 1) throw new IllegalArgumentException("use - or +");
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("use - or +");
    }
}
