import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import java.nio.file.*;
import java.util.*;

class WordNetTest {
    @TempDir Path directory;
    static int[][] floyd(Digraph graph) {
        int n=graph.V();int[][] d=new int[n][n];
        for(int v=0;v<n;v++) {
            Arrays.fill(d[v],1000000);d[v][v]=0;
            for(int w:graph.adj(v))d[v][w]=Math.min(d[v][w],1);
        }
        for(int k=0;k<n;k++)for(int v=0;v<n;v++)for(int w=0;w<n;w++)d[v][w]=Math.min(d[v][w],d[v][k]+d[k][w]);
        return d;
    }
    static void compare(SAP sap,int[][] d,List<Integer> a,List<Integer> b) {
        int optimum=1000000;
        for(int v:a)for(int w:b)for(int x=0;x<d.length;x++)optimum=Math.min(optimum,d[v][x]+d[w][x]);
        int length=sap.length(a,b),ancestor=sap.ancestor(a,b);
        if(optimum>=1000000){assertEquals(-1,length);assertEquals(-1,ancestor);}
        else {
            assertEquals(optimum,length);assertTrue(ancestor>=0 && ancestor<d.length);
            int da=1000000,db=1000000;for(int v:a)da=Math.min(da,d[v][ancestor]);for(int w:b)db=Math.min(db,d[w][ancestor]);
            assertEquals(optimum,da+db);
        }
    }
    @Test void randomDirectedGraphsAgainstFloydWarshall() {
        Random random=new Random(20261103);
        for(int trial=0;trial<100;trial++) {
            int n=2+random.nextInt(13);Digraph graph=new Digraph(n);
            for(int v=0;v<n;v++)for(int w=0;w<n;w++)if(random.nextDouble()<.15)graph.addEdge(v,w);
            int[][] d=floyd(graph);SAP sap=new SAP(graph);
            for(int v=0;v<n;v++)for(int w=0;w<n;w++) {
                compare(sap,d,List.of(v),List.of(w));
                assertEquals(sap.length(List.of(v),List.of(w)),sap.length(v,w));
                int ancestor=sap.ancestor(v,w);assertEquals(sap.ancestor(List.of(v),List.of(w)),ancestor);
            }
            for(int i=0;i<20;i++) {
                List<Integer> a=new ArrayList<>(),b=new ArrayList<>();
                for(int j=0;j<5;j++){if(random.nextBoolean())a.add(random.nextInt(n));if(random.nextBoolean())b.add(random.nextInt(n));}
                compare(sap,d,a,b);
            }
        }
    }
    @Test void sapValidationCopyAndOnePassIterables() {
        assertThrows(IllegalArgumentException.class,()->new SAP(null));
        Digraph graph=new Digraph(3);graph.addEdge(0,1);SAP sap=new SAP(graph);graph.addEdge(2,1);
        assertEquals(-1,sap.length(0,2));
        assertThrows(IllegalArgumentException.class,()->sap.length(-1,0));
        assertThrows(IllegalArgumentException.class,()->sap.ancestor(0,3));
        assertThrows(IllegalArgumentException.class,()->sap.length(null,List.of(0)));
        assertThrows(IllegalArgumentException.class,()->sap.ancestor(List.of(0),null));
        assertThrows(IllegalArgumentException.class,()->sap.length(Arrays.asList(0,null),List.of(0)));
        assertThrows(IllegalArgumentException.class,()->sap.ancestor(List.of(),List.of(3)));
        assertEquals(-1,sap.length(List.of(),List.of(1)));
        assertEquals(-1,new SAP(new Digraph(0)).ancestor(List.of(),List.of()));
        Iterable<Integer> once=new Iterable<>() {
            boolean used;
            public Iterator<Integer> iterator(){assertFalse(used);used=true;return List.of(0,0).iterator();}
        };
        assertEquals(1,sap.length(once,List.of(1)));
    }
    @Test void officialDigraphSample() {
        SAP sap=new SAP(new Digraph(new In("digraph1.txt")));
        assertEquals(4,sap.length(3,11));assertEquals(1,sap.ancestor(3,11));
        assertEquals(3,sap.length(9,12));assertEquals(5,sap.ancestor(9,12));
        assertEquals(4,sap.length(7,2));assertEquals(0,sap.ancestor(7,2));
        assertEquals(-1,sap.length(1,6));assertEquals(-1,sap.ancestor(1,6));
    }
    WordNet fixture(String sets,String edges) throws Exception {
        Path s=directory.resolve("synsets.txt"),h=directory.resolve("hypernyms.txt");
        Files.writeString(s,sets);Files.writeString(h,edges);return new WordNet(s.toString(),h.toString());
    }
    @Test void polysemySynonymsAndNounValidation() throws Exception {
        WordNet wn=fixture("0,entity,root,with comma\n1,animal creature,animals\n2,dog canine,dogs\n3,cat feline,cats\n4,table furniture,tables\n5,dog hot_dog,food\n6,puppy,pups\n",
                "0\n1,0\n2,1\n3,1\n4,0\n5,4\n6,2\n");
        assertEquals(2,wn.distance("dog","cat"));assertEquals(0,wn.distance("dog","canine"));
        assertEquals(1,wn.distance("dog","table"));assertEquals("table furniture",wn.sap("dog","table"));
        assertEquals(4,wn.distance("puppy","table"));assertEquals(1,wn.distance("puppy","dog"));
        assertEquals("animal creature",wn.sap("dog","cat"));assertTrue(wn.isNoun("hot_dog"));assertFalse(wn.isNoun("missing"));
        Set<String> nouns=new HashSet<>();wn.nouns().forEach(nouns::add);assertEquals(11,nouns.size());
        Iterator<String> iterator=wn.nouns().iterator();iterator.next();assertThrows(UnsupportedOperationException.class,iterator::remove);
        assertThrows(IllegalArgumentException.class,()->wn.isNoun(null));
        assertThrows(IllegalArgumentException.class,()->wn.distance("dog",null));
        assertThrows(IllegalArgumentException.class,()->wn.sap("missing","dog"));
        assertThrows(IllegalArgumentException.class,()->new WordNet(null,"hypernyms.txt"));
        assertThrows(IllegalArgumentException.class,()->new WordNet("synsets.txt",null));
        assertEquals("cat",new Outcast(wn).outcast(new String[]{"dog","cat","table"}));
    }
    @Test void rootedDagRejectionAndLongChain() throws Exception {
        for(String edges:new String[]{"0,1\n1,0\n2\n","0\n1\n2,0\n","0,1\n1,2\n2,0\n","0,0\n1,0\n2,1\n"})
            assertThrows(IllegalArgumentException.class,()->fixture("0,a,x\n1,b,x\n2,c,x\n",edges));
        for(String suffix:new String[]{"3InvalidCycle","3InvalidTwoRoots","6InvalidCycle","6InvalidCycle+Path","6InvalidTwoRoots"}) {
            String n=suffix.startsWith("3")?"3":"6";
            assertThrows(IllegalArgumentException.class,()->new WordNet("synsets"+n+".txt","hypernyms"+suffix+".txt"));
        }
        StringBuilder sets=new StringBuilder(),edges=new StringBuilder();
        for(int i=0;i<20000;i++) {
            sets.append(i).append(",n").append(i).append(",definition\n");
            edges.append(i);if(i<19999)edges.append(',').append(i+1);edges.append('\n');
        }
        WordNet chain=fixture(sets.toString(),edges.toString());assertEquals(19999,chain.distance("n0","n19999"));
        assertEquals("n19999",chain.sap("n0","n19999"));
    }
    @Test void fullOfficialWordnetOutcasts() {
        WordNet wn=new WordNet("synsets.txt","hypernyms.txt");Outcast outcast=new Outcast(wn);
        for(String[] example:new String[][]{{"outcast5.txt","table"},{"outcast8.txt","bed"},{"outcast11.txt","potato"}}) {
            In input=new In(example[0]);assertEquals(example[1],outcast.outcast(input.readAllStrings()));input.close();
        }
        int count=0;for(String noun:wn.nouns()){assertTrue(wn.isNoun(noun));count++;}
        assertTrue(count>100000);assertEquals(0,wn.distance("bird","bird"));
        assertThrows(IllegalArgumentException.class,()->new Outcast(null));
    }
}
