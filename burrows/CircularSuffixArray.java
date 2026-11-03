public class CircularSuffixArray {
    private final int[] indices;
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("null string");
        int n = s.length();
        indices = new int[n];
        if (n == 0) return;
        int alphabet = 1;
        for (int i = 0; i < n; i++) alphabet = Math.max(alphabet, s.charAt(i)+1);
        int[] counts = new int[Math.max(alphabet, n)+1];
        for (int i = 0; i < n; i++) counts[s.charAt(i)+1]++;
        for (int c = 1; c <= alphabet; c++) counts[c] += counts[c-1];
        for (int i = 0; i < n; i++) indices[counts[s.charAt(i)]++] = i;
        int[] ranks = new int[n], nextRanks = new int[n], shifted = new int[n];
        int classes = 1;
        for (int i = 1; i < n; i++) {
            if (s.charAt(indices[i]) != s.charAt(indices[i-1])) classes++;
            ranks[indices[i]] = classes-1;
        }
        for (int span = 1; span < n && classes < n;) {
            java.util.Arrays.fill(counts, 0, classes+1, 0);
            for (int i = 0; i < n; i++) {
                shifted[i] = indices[i]-span;
                if (shifted[i] < 0) shifted[i] += n;
                counts[ranks[shifted[i]]+1]++;
            }
            for (int i = 1; i <= classes; i++) counts[i] += counts[i-1];
            for (int i = 0; i < n; i++) indices[counts[ranks[shifted[i]]]++] = shifted[i];
            int nextClasses = 1;
            nextRanks[indices[0]] = 0;
            for (int i = 1; i < n; i++) {
                int a = indices[i-1], b = indices[i];
                if (ranks[a] != ranks[b] || ranks[(a+span)%n] != ranks[(b+span)%n]) nextClasses++;
                nextRanks[b] = nextClasses-1;
            }
            int[] swap = ranks; ranks = nextRanks; nextRanks = swap;
            classes = nextClasses;
            if (span >= n-span) break;
            span *= 2;
        }
    }
    public int length() { return indices.length; }
    public int index(int i) {
        if (i < 0 || i >= indices.length) throw new IllegalArgumentException("invalid suffix index");
        return indices[i];
    }
    public static void main(String[] args) {
        CircularSuffixArray suffixes = new CircularSuffixArray(args.length == 0 ? "ABRACADABRA!" : args[0]);
        System.out.println("length = " + suffixes.length());
        for (int i = 0; i < suffixes.length(); i++) System.out.println(suffixes.index(i));
    }
}
