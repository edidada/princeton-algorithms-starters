import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.util.*;

class BaseballTest {
    @TempDir Path directory;
    static List<String> list(Iterable<String> values) {
        List<String> result=new ArrayList<>();values.forEach(result::add);return result;
    }
    static void certificate(BaseballElimination league,String team) {
        Iterable<String> proof=league.certificateOfElimination(team);
        if(!league.isEliminated(team)){assertNull(proof);return;}
        assertNotNull(proof);List<String> r=list(proof);assertFalse(r.isEmpty());
        assertEquals(r.size(),new HashSet<>(r).size());assertFalse(r.contains(team));
        long forced=0;
        for(int i=0;i<r.size();i++) {
            forced+=league.wins(r.get(i));
            for(int j=i+1;j<r.size();j++)forced+=league.against(r.get(i),r.get(j));
        }
        assertTrue(forced>(long)r.size()*((long)league.wins(team)+league.remaining(team)),"invalid elimination proof for "+team);
    }
    BaseballElimination fixture(int[] wins,int[] outside,int[][] games) throws Exception {
        int n=wins.length;StringBuilder text=new StringBuilder().append(n).append('\n');
        for(int i=0;i<n;i++) {
            int remaining=outside[i];for(int g:games[i])remaining+=g;
            text.append("T").append(i).append(' ').append(wins[i]).append(" 0 ").append(remaining);
            for(int g:games[i])text.append(' ').append(g);text.append('\n');
        }
        Path file=directory.resolve("teams.txt");Files.writeString(file,text);return new BaseballElimination(file.toString());
    }
    // Enumerate every distribution of wins for the remaining non-target games.
    static boolean feasible(int x,int maximum,int[] scores,int[][] games,int pair) {
        int n=scores.length;
        for(int i=0;i<n;i++)if(i!=x && scores[i]>maximum)return false;
        for(int p=pair;p<n*n;p++) {
            int a=p/n,b=p%n;
            if(a>=b || a==x || b==x || games[a][b]==0)continue;
            for(int won=0;won<=games[a][b];won++) {
                scores[a]+=won;scores[b]+=games[a][b]-won;
                boolean possible=feasible(x,maximum,scores,games,p+1);
                scores[a]-=won;scores[b]-=games[a][b]-won;
                if(possible)return true;
            }
            return false;
        }
        return true;
    }
    @Test void officialFourAndFiveTeamExamples() {
        BaseballElimination four=new BaseballElimination("teams4.txt");
        assertEquals(4,four.numberOfTeams());assertEquals(83,four.wins("Atlanta"));assertEquals(71,four.losses("Atlanta"));
        assertEquals(8,four.remaining("Atlanta"));assertEquals(6,four.against("Atlanta","New_York"));
        assertFalse(four.isEliminated("Atlanta"));assertFalse(four.isEliminated("New_York"));
        assertTrue(four.isEliminated("Philadelphia"));assertTrue(four.isEliminated("Montreal"));
        assertEquals(Set.of("Atlanta","New_York"),new HashSet<>(list(four.certificateOfElimination("Philadelphia"))));
        assertEquals(List.of("Atlanta"),list(four.certificateOfElimination("Montreal")));
        BaseballElimination five=new BaseballElimination("teams5.txt");
        for(String team:five.teams()) {assertEquals(team.equals("Detroit"),five.isEliminated(team));certificate(five,team);}
        assertEquals(Set.of("New_York","Baltimore","Boston","Toronto"),new HashSet<>(list(five.certificateOfElimination("Detroit"))));
    }
    @Test void randomSchedulesAgainstExhaustiveOutcomes() throws Exception {
        Random random=new Random(20261103);
        for(int trial=0;trial<250;trial++) {
            int n=1+random.nextInt(5);int[] wins=new int[n],outside=new int[n];int[][] games=new int[n][n];
            for(int i=0;i<n;i++) {
                wins[i]=random.nextInt(10);outside[i]=random.nextInt(3);
                for(int j=0;j<i;j++)games[i][j]=games[j][i]=random.nextInt(2);
            }
            BaseballElimination league=fixture(wins,outside,games);
            for(int x=0;x<n;x++) {
                int maximum=wins[x]+outside[x];for(int g:games[x])maximum+=g;
                boolean eliminated=!feasible(x,maximum,wins.clone(),games,0);
                assertEquals(eliminated,league.isEliminated("T"+x),"trial "+trial+" team "+x);
                certificate(league,"T"+x);
                assertEquals(eliminated,league.isEliminated("T"+x));
            }
        }
    }
    @Test void tiesOutsideGamesSingletonAndLargeTotals() throws Exception {
        BaseballElimination one=new BaseballElimination("teams1.txt");assertFalse(one.isEliminated("Turing"));
        BaseballElimination tied=fixture(new int[]{10,10},new int[2],new int[2][2]);
        assertFalse(tied.isEliminated("T0"));assertFalse(tied.isEliminated("T1"));
        BaseballElimination outside=fixture(new int[]{2,4},new int[]{2,0},new int[2][2]);assertFalse(outside.isEliminated("T0"));
        BaseballElimination large=fixture(new int[]{2_000_000_001,2_000_000_000},new int[]{1_000_000_000,0},new int[2][2]);
        assertFalse(large.isEliminated("T0"));assertTrue(large.isEliminated("T1"));certificate(large,"T1");
        BaseballElimination many=fixture(new int[]{3,3,3},new int[3],new int[][]{{0,2,2},{2,0,2},{2,2,0}});
        for(int x=0;x<3;x++)assertFalse(many.isEliminated("T"+x));
    }
    @Test void invalidTeamsAndReadOnlyResults() {
        BaseballElimination league=new BaseballElimination("teams4.txt");
        for(String invalid:new String[]{null,"missing"}) {
            assertThrows(IllegalArgumentException.class,()->league.wins(invalid));
            assertThrows(IllegalArgumentException.class,()->league.losses(invalid));
            assertThrows(IllegalArgumentException.class,()->league.remaining(invalid));
            assertThrows(IllegalArgumentException.class,()->league.against(invalid,"Atlanta"));
            assertThrows(IllegalArgumentException.class,()->league.against("Atlanta",invalid));
            assertThrows(IllegalArgumentException.class,()->league.isEliminated(invalid));
            assertThrows(IllegalArgumentException.class,()->league.certificateOfElimination(invalid));
        }
        Iterator<String> teams=league.teams().iterator();teams.next();assertThrows(UnsupportedOperationException.class,teams::remove);
        Iterator<String> proof=league.certificateOfElimination("Philadelphia").iterator();proof.next();assertThrows(UnsupportedOperationException.class,proof::remove);
    }
    @Test void officialDivisionCertificates() {
        for(String filename:new String[]{"teams4a.txt","teams4b.txt","teams5a.txt","teams5b.txt","teams5c.txt","teams12.txt","teams24.txt","teams60.txt"}) {
            BaseballElimination league=new BaseballElimination(filename);
            for(String team:league.teams())certificate(league,team);
        }
    }
}
