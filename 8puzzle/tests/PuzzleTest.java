import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.nio.file.*;

class PuzzleTest {
    static Board from(String state, int n) {
        int[][] tiles = new int[n][n];
        for (int i=0;i<state.length();i++) tiles[i/n][i%n]=state.charAt(i)-'0';
        return new Board(tiles);
    }
    static List<String> adjacent(String state, int n) {
        int z=state.indexOf('0'); List<String> result=new ArrayList<>();
        for(int i=0;i<state.length();i++) {
            if(Math.abs(i/n-z/n)+Math.abs(i%n-z%n)!=1) continue;
            char[] s=state.toCharArray();s[z]=s[i];s[i]='0';result.add(new String(s));
        }
        return result;
    }
    static Map<String,Integer> bfs(String goal,int n,int depth) {
        Map<String,Integer> distances=new HashMap<>();ArrayDeque<String> queue=new ArrayDeque<>();
        queue.add(goal);distances.put(goal,0);
        while(!queue.isEmpty()) {
            String current=queue.remove();int d=distances.get(current);
            if(d==depth) continue;
            for(String next:adjacent(current,n)) if(!distances.containsKey(next)) {
                distances.put(next,d+1);queue.add(next);
            }
        }
        return distances;
    }
    static String encode(Board board) {
        Scanner scanner=new Scanner(board.toString());int n=scanner.nextInt();StringBuilder s=new StringBuilder();
        for(int i=0;i<n*n;i++)s.append((char)('0'+scanner.nextInt()));return s.toString();
    }
    static void assertSolution(Board initial, int optimum) {
        Solver solver=new Solver(initial);
        assertEquals(optimum,solver.moves());assertEquals(optimum>=0,solver.isSolvable());
        if(optimum<0) { assertNull(solver.solution());return; }
        List<Board> path=new ArrayList<>();solver.solution().forEach(path::add);
        assertEquals(optimum+1,path.size());assertEquals(initial,path.get(0));
        assertTrue(path.get(path.size()-1).isGoal());
        for(int i=1;i<path.size();i++) {
            String before=encode(path.get(i-1)),after=encode(path.get(i));
            assertTrue(adjacent(before,initial.dimension()).contains(after),"illegal transition");
        }
        assertEquals(path.size(),new HashSet<>(path).size());
    }
    @Test void boardContracts() {
        int[][] tiles={{8,1,3},{4,0,2},{7,6,5}};
        Board b=new Board(tiles);tiles[0][0]=0;
        assertEquals(3,b.dimension());assertEquals(5,b.hamming());assertEquals(10,b.manhattan());
        assertFalse(b.isGoal());assertEquals(b,from("813402765",3));assertEquals(b.hashCode(),from("813402765",3).hashCode());
        assertNotEquals(b,null);assertNotEquals(b,"board");assertNotEquals(b,from("1230",2));
        Set<String> actual=new HashSet<>();b.neighbors().forEach(x->actual.add(encode(x)));
        assertEquals(new HashSet<>(adjacent("813402765",3)),actual);
        String twin=encode(b.twin());assertEquals('0',twin.charAt(4));
        assertEquals(2,java.util.stream.IntStream.range(0,9).filter(i->twin.charAt(i)!="813402765".charAt(i)).count());
        assertEquals("813402765",encode(b));
        assertThrows(IllegalArgumentException.class,()->new Solver(null));
    }
    static void permutations(String prefix,String remaining,List<String> output) {
        if(remaining.isEmpty()) { output.add(prefix);return; }
        for(int i=0;i<remaining.length();i++) permutations(prefix+remaining.charAt(i),remaining.substring(0,i)+remaining.substring(i+1),output);
    }
    @Test void exhaustiveTwoByTwo() {
        Map<String,Integer> optimum=bfs("1230",2,100);
        assertEquals(12,optimum.size());List<String> all=new ArrayList<>();permutations("","0123",all);
        assertEquals(24,all.size());
        for(String state:all)assertSolution(from(state,2),optimum.getOrDefault(state,-1));
    }
    @Test void breadthFirstIndependentThreeByThree() {
        Map<String,Integer> optimum=bfs("123456780",3,16);
        List<String> states=new ArrayList<>(optimum.keySet());Collections.sort(states);
        Collections.shuffle(states,new Random(1103));
        for(String state:states.subList(0,100))assertSolution(from(state,3),optimum.get(state));
    }
    @Test void officialKnownDistanceFixtures() throws Exception {
        for(int moves=0;moves<=31;moves++) {
            Path file=Path.of(String.format("puzzle3x3-%02d.txt",moves));
            try(Scanner s=new Scanner(file)) {
                int n=s.nextInt();int[][] t=new int[n][n];
                for(int i=0;i<n;i++)for(int j=0;j<n;j++)t[i][j]=s.nextInt();
                assertSolution(new Board(t),moves);
            }
        }
        assertSolution(from("123456870",3),-1);
    }
    @Test void generalizedBoards() {
        for(int n:new int[]{4,5,10}) {
            int[][] t=new int[n][n];for(int i=0;i<n*n;i++)t[i/n][i%n]=i+1;
            t[n-1][n-1]=t[n-1][n-2];t[n-1][n-2]=t[n-1][n-3];t[n-1][n-3]=0;
            Board b=new Board(t);Solver s=new Solver(b);
            assertEquals(2,s.moves());assertEquals(2,b.manhattan());
            int count=0;for(Board step:s.solution()) { count++;assertEquals(n,step.dimension()); }
            assertEquals(3,count);
        }
    }
}
