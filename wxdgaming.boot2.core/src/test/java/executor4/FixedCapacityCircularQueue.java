package executor4;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * 非线程安全、定容、循环队列
 * 特点：
 * 1. 固定容量，不会自动扩容
 * 2. 非线程安全，单线程使用
 * 3. 环形数组实现，无元素拷贝
 * 4. 主动清理引用，避免GC问题
 *
 * @param <T> 元素类型
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2026-06-04 19:34
 */
public class FixedCapacityCircularQueue<T> implements Iterable<T> {

    private final Object[] elements;
    private final int capacity;
    private int head;
    private int tail;
    private int size;

    public FixedCapacityCircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("容量必须大于0: " + capacity);
        }
        this.capacity = capacity;
        this.elements = new Object[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    /**
     * 添加元素到尾部
     *
     * @return 添加成功返回true，队列满返回false
     */
    public boolean offer(T element) {
        if (element == null) {
            throw new NullPointerException("不支持null元素");
        }
        if (isFull()) {
            return false;
        }
        elements[tail] = element;
        tail = (tail + 1) % capacity;
        size++;
        return true;
    }

    /**
     * 移除并返回头部元素
     *
     * @return 头部元素，队列为空返回null
     */
    @SuppressWarnings("unchecked")
    public T poll() {
        if (isEmpty()) {
            return null;
        }
        T result = (T) elements[head];
        elements[head] = null;  // 主动置空，帮助GC
        head = (head + 1) % capacity;
        size--;
        return result;
    }

    /**
     * 查看头部元素（不移除）
     *
     * @return 头部元素，队列为空返回null
     */
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return (T) elements[head];
    }

    /**
     * 清空队列
     */
    public void clear() {
        if (head < tail) {
            Arrays.fill(elements, head, tail, null);
        } else if (head > tail) {
            Arrays.fill(elements, head, capacity, null);
            Arrays.fill(elements, 0, tail, null);
        }
        head = tail = size = 0;
    }

    /**
     * 检查是否包含某个元素
     */
    public boolean contains(Object o) {
        if (o == null || isEmpty()) {
            return false;
        }
        int current = head;
        for (int i = 0; i < size; i++) {
            if (o.equals(elements[current])) {
                return true;
            }
            current = (current + 1) % capacity;
        }
        return false;
    }

    // ========== 查询方法 ==========

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public int capacity() {
        return capacity;
    }

    public int remainingCapacity() {
        return capacity - size;
    }

    // ========== 遍历 ==========

    public void forEach(java.util.function.Consumer<? super T> action) {
        int current = head;
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            T element = (T) elements[current];
            action.accept(element);
            current = (current + 1) % capacity;
        }
    }

    public Object[] toArray() {
        Object[] result = new Object[size];
        int current = head;
        for (int i = 0; i < size; i++) {
            result[i] = elements[current];
            current = (current + 1) % capacity;
        }
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new CircularIterator();
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        int current = head;
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[current]);
            current = (current + 1) % capacity;
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FixedCapacityCircularQueue<?> that = (FixedCapacityCircularQueue<?>) o;
        if (size != that.size) return false;

        Iterator<T> it1 = this.iterator();
        Iterator<?> it2 = that.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            if (!Objects.equals(it1.next(), it2.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (T element : this) {
            result = 31 * result + (element != null ? element.hashCode() : 0);
        }
        return result;
    }

    // ========== 内部迭代器 ==========

    private class CircularIterator implements Iterator<T> {
        private int cursor = head;
        private int remaining = size;

        @Override
        public boolean hasNext() {
            return remaining > 0;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            if (remaining == 0) {
                throw new NoSuchElementException();
            }
            T result = (T) elements[cursor];
            cursor = (cursor + 1) % capacity;
            remaining--;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("迭代器不支持remove操作");
        }
    }
}
