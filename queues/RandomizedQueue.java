import edu.princeton.cs.algs4.StdRandom;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** Resizing dense array; swap-delete provides uniform amortized O(1) removal. */
public final class RandomizedQueue<Item> implements Iterable<Item> {
    private Object[] items = new Object[2];
    private int size;

    public RandomizedQueue() { }
    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }

    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException("Null item");
        if (size == items.length) resize(items.length * 2);
        items[size++] = item;
    }

    @SuppressWarnings("unchecked")
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException();
        int index = StdRandom.uniform(size);
        Item result = (Item) items[index];
        items[index] = items[--size];
        items[size] = null;
        if (size > 0 && size <= items.length / 4) resize(items.length / 2);
        return result;
    }

    @SuppressWarnings("unchecked")
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException();
        return (Item) items[StdRandom.uniform(size)];
    }

    private void resize(int capacity) {
        Object[] copy = new Object[Math.max(2, capacity)];
        System.arraycopy(items, 0, copy, 0, size);
        items = copy;
    }

    public Iterator<Item> iterator() {
        Object[] snapshot = new Object[size];
        System.arraycopy(items, 0, snapshot, 0, size);
        StdRandom.shuffle(snapshot);
        return new Iterator<>() {
            private int position;
            public boolean hasNext() { return position < snapshot.length; }
            @SuppressWarnings("unchecked")
            public Item next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (Item) snapshot[position++];
            }
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }

    public static void main(String[] args) {
        RandomizedQueue<String> queue = new RandomizedQueue<>();
        for (String arg : args) queue.enqueue(arg);
        while (!queue.isEmpty()) System.out.println(queue.dequeue());
    }
}
