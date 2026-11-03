import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import edu.princeton.cs.algs4.In;
import java.util.*;

class BoggleTest {
    static Set<String> words(Iterable<String> found) {
        Set<String> set=new HashSet<>();for(String word:found)assertTrue(set.add(word),"duplicate word");return set;
    }
    // Enumerate all simple king-move paths without a dictionary or prefix pruning.
    static void enumerate(char[][] board,int r,int c,long used,String prefix,Set<String> paths) {
        int n=board[0].length,bit=r*n+c;if((used&(1L<<bit))!=0)return;
        String word=prefix+(board[r][c]=='Q'?"QU":String.valueOf(board[r][c]));
        paths.add(word);used|=1L<<bit;
        for(int nr=0;nr<board.length;nr++)for(int nc=0;nc<n;nc++)
            if(Math.max(Math.abs(nr-r),Math.abs(nc-c))==1)enumerate(board,nr,nc,used,word,paths);
    }
    @Test void exhaustiveSmallBoardsIndependentOracle() {
        Random random=new Random(1103);char[] alphabet={'A','B','C','Q','U'};
        for(int trial=0;trial<100;trial++) {
            char[][] board=new char[2][3];
            for(int r=0;r<2;r++)for(int c=0;c<3;c++)board[r][c]=alphabet[random.nextInt(alphabet.length)];
            Set<String> paths=new HashSet<>();for(int r=0;r<2;r++)for(int c=0;c<3;c++)enumerate(board,r,c,0,"",paths);
            List<String> sorted=new ArrayList<>(paths);Collections.sort(sorted);Collections.shuffle(sorted,random);
            Set<String> dictionary=new HashSet<>(sorted.subList(0,Math.min(150,sorted.size())));
            for(int i=0;i<150;i++) {
                StringBuilder word=new StringBuilder();for(int j=random.nextInt(8)+1;j>0;j--)word.append(alphabet[random.nextInt(alphabet.length)]);
                dictionary.add(word.toString());
            }
            Set<String> expected=new HashSet<>(paths);expected.retainAll(dictionary);expected.removeIf(w->w.length()<3);
            BoggleSolver solver=new BoggleSolver(dictionary.toArray(new String[0]));
            assertEquals(expected,words(solver.getAllValidWords(new BoggleBoard(board))),"trial "+trial);
        }
    }
    @Test void quAdjacencyNoReuseAndDeduplication() {
        BoggleSolver solver=new BoggleSolver(new String[]{"QU","QUA","QUAT","QAT","QUQU","AAA","AAAA","AAAAA","ABC","ABC"});
        assertEquals(Set.of("QUA","QUAT"),words(solver.getAllValidWords(new BoggleBoard(new char[][]{{'Q','A','T'}}))));
        assertEquals(1,solver.scoreOf("QAT")); // Dictionary membership does not require a board path.
        assertEquals(Set.of("QUQU"),words(solver.getAllValidWords(new BoggleBoard(new char[][]{{'Q','Q'}}))));
        assertEquals(Set.of("AAA","AAAA"),words(solver.getAllValidWords(new BoggleBoard(new char[][]{{'A','A'},{'A','A'}}))));
        BoggleBoard diagonal=new BoggleBoard(new char[][]{{'A','X','X'},{'X','B','X'},{'X','X','C'}});
        assertEquals(Set.of("ABC"),words(solver.getAllValidWords(diagonal)));
        assertTrue(words(solver.getAllValidWords(new BoggleBoard(new char[][]{{'A','X','B','C'}}))).isEmpty());
    }
    @Test void scoringAndIndependentCalls() {
        String[] dictionary={"A","AA","AAA","AAAA","AAAAA","AAAAAA","AAAAAAA","AAAAAAAA","ABCDEFGHI"};
        BoggleSolver solver=new BoggleSolver(dictionary);dictionary[2]="MUTATED";
        int[] expected={0,0,1,1,2,3,5,11,11};
        String[] original={"A","AA","AAA","AAAA","AAAAA","AAAAAA","AAAAAAA","AAAAAAAA","ABCDEFGHI"};
        for(int i=0;i<expected.length;i++)assertEquals(expected[i],solver.scoreOf(original[i]));
        assertEquals(0,solver.scoreOf("ABSENT"));assertEquals(0,solver.scoreOf(""));
        BoggleBoard board=new BoggleBoard(new char[][]{{'A','A','A'}});
        Iterable<String> first=solver.getAllValidWords(board);Iterator<String> iterator=first.iterator();iterator.next();iterator.remove();
        assertEquals(Set.of("AAA"),words(solver.getAllValidWords(board)));
        assertThrows(IllegalArgumentException.class,()->solver.getAllValidWords(null));
        assertThrows(IllegalArgumentException.class,()->solver.scoreOf(null));
        assertThrows(IllegalArgumentException.class,()->new BoggleSolver(null));
    }
    @Test void officialAlgs4Scores() {
        In input=new In("dictionary-algs4.txt");BoggleSolver solver=new BoggleSolver(input.readAllStrings());input.close();
        assertEquals(33,score(solver,"board4x4.txt"));assertEquals(84,score(solver,"board-q.txt"));
    }
    static int score(BoggleSolver solver,String file) {
        int sum=0;for(String word:words(solver.getAllValidWords(new BoggleBoard(file))))sum+=solver.scoreOf(word);return sum;
    }
    @Test void officialYawlStressBoards() {
        In input=new In("dictionary-yawl.txt");BoggleSolver solver=new BoggleSolver(input.readAllStrings());input.close();
        for(int expected:new int[]{0,1,2,3,4,5,100,1000,4410,4540})
            assertEquals(expected,score(solver,"board-points"+expected+".txt"));
        assertEquals(0,score(solver,"board-16q.txt"));
    }
}
