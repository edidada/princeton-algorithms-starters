import java.util.Iterator;
import java.util.NoSuchElementException;

/** Doubly linked deque with constant-time operations at both ends. */
public final class Deque<Item> implements Iterable<Item> {
    private static final class Node<T> {
        T item;
        Node<T> previous;
        Node<T> next;
        Node(T value) { item = value; }
    }
    private Node<Item> first;
    private Node<Item> last;
    private int size;

    public Deque() { }
    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }

    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException("Null item");
        Node<Item> node = new Node<>(item);
        node.next = first;
        if (first == null) last = node;
        else first.previous = node;
        first = node;
        size++;
    }

    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException("Null item");
        Node<Item> node = new Node<>(item);
        node.previous = last;
        if (last == null) first = node;
        else last.next = node;
        last = node;
        size++;
    }

    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException();
        Item result = first.item;
        Node<Item> next = first.next;
        first.next = null;
        first.item = null;
        first = next;
        if (first == null) last = null;
        else first.previous = null;
        size--;
        return result;
    }

    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException();
        Item result = last.item;
        Node<Item> previous = last.previous;
        last.previous = null;
        last.item = null;
        last = previous;
        if (last == null) first = null;
        else last.next = null;
        size--;
        return result;
    }

    public Iterator<Item> iterator() {
        return new Iterator<>() {
            private Node<Item> current = first;
            public boolean hasNext() { return current != null; }
            public Item next() {
                if (!hasNext()) throw new NoSuchElementException();
                Item result = current.item;
                current = current.next;
                return result;
            }
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }

    public static void main(String[] args) {
        Deque<String> deque = new Deque<>();
        for (String arg : args) deque.addLast(arg);
        for (String item : deque) System.out.println(item);
    }
}
