import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class QueuesTest {
    @Test void dequeAgainstJdkOracle() {
        Random random = new Random(2002);
        Deque<Integer> actual = new Deque<>();
        ArrayDeque<Integer> expected = new ArrayDeque<>();
        for (int step = 0; step < 25000; step++) {
            int operation = random.nextInt(4), value = random.nextInt();
            if (expected.isEmpty() || operation == 0) {
                actual.addFirst(value); expected.addFirst(value);
            } else if (operation == 1) {
                actual.addLast(value); expected.addLast(value);
            } else if (operation == 2) {
                assertEquals(expected.removeFirst(), actual.removeFirst());
            } else {
                assertEquals(expected.removeLast(), actual.removeLast());
            }
            assertEquals(expected.size(), actual.size());
            assertEquals(expected.isEmpty(), actual.isEmpty());
            List<Integer> snapshot = new ArrayList<>();
            actual.forEach(snapshot::add);
            assertEquals(new ArrayList<>(expected), snapshot);
        }
    }

    @Test void exceptionsAndEmptyTransitions() {
        Deque<Integer> deque = new Deque<>();
        assertThrows(IllegalArgumentException.class, () -> deque.addFirst(null));
        assertThrows(IllegalArgumentException.class, () -> deque.addLast(null));
        assertThrows(NoSuchElementException.class, deque::removeFirst);
        assertThrows(NoSuchElementException.class, deque::removeLast);
        assertThrows(NoSuchElementException.class, deque.iterator()::next);
        assertThrows(UnsupportedOperationException.class, deque.iterator()::remove);
        deque.addFirst(1); assertEquals(1, deque.removeLast());
        deque.addLast(2); assertEquals(2, deque.removeFirst());
        assertTrue(deque.isEmpty());
        RandomizedQueue<Integer> queue = new RandomizedQueue<>();
        assertThrows(IllegalArgumentException.class, () -> queue.enqueue(null));
        assertThrows(NoSuchElementException.class, queue::sample);
        assertThrows(NoSuchElementException.class, queue::dequeue);
        assertThrows(NoSuchElementException.class, queue.iterator()::next);
        assertThrows(UnsupportedOperationException.class, queue.iterator()::remove);
    }

    @Test void randomizedQueueGrowthShrinkAndSnapshots() {
        RandomizedQueue<Integer> queue = new RandomizedQueue<>();
        for (int round = 0; round < 10; round++) {
            Set<Integer> expected = new HashSet<>();
            for (int i = 0; i < 1000; i++) { queue.enqueue(i); expected.add(i); }
            Iterator<Integer> first = queue.iterator(), second = queue.iterator();
            assertTrue(expected.contains(queue.sample()));
            assertEquals(1000, queue.size());
            while (!queue.isEmpty()) assertTrue(expected.remove(queue.dequeue()));
            assertTrue(expected.isEmpty());
            Set<Integer> a = new HashSet<>(), b = new HashSet<>();
            while (first.hasNext()) assertTrue(a.add(first.next()));
            while (second.hasNext()) assertTrue(b.add(second.next()));
            assertEquals(1000, a.size()); assertEquals(a, b);
        }
    }

    @Test void sampleUniformityAndIteratorPermutations() {
        RandomizedQueue<Integer> queue = new RandomizedQueue<>();
        for (int i = 0; i < 3; i++) queue.enqueue(i);
        int[] counts = new int[3];
        for (int i = 0; i < 6000; i++) counts[queue.sample()]++;
        for (int count : counts) assertTrue(count > 1000 && count < 3000);
        Set<List<Integer>> orders = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            List<Integer> order = new ArrayList<>(); queue.forEach(order::add); orders.add(order);
        }
        assertEquals(6, orders.size());
        assertEquals(3, queue.size());
    }
}
