package wxdgaming.boot2.core.collection;

import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * 元素替换
 * <p>后面加入的元素是会替换前面的元素
 * <p>请注意，替换规则是调用 在调用 hashcode equals 方法
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-02-16 10:46
 **/
public class ConvertCollection<E> extends ObjectBase {

    private final ReentrantLock lock = new ReentrantLock();
    private LinkedHashSet<E> nodes = new LinkedHashSet<>();

    /**
     * 元素替换
     * <p>后面加入的元素是会替换前面的元素
     * <p>请注意，替换规则是调用 在调用 hashcode equals 方法
     *
     * @param e 模型
     */
    public void add(E e) {
        lock.lock();
        try {
            nodes.remove(e);
            nodes.add(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 元素替换
     * <p>后面加入的元素是会替换前面的元素
     * <p>请注意，替换规则是调用 在调用 hashcode equals 方法
     *
     * @param es 模型
     */
    public void addAll(Collection<E> es) {
        lock.lock();
        try {
            nodes.removeAll(es);
            nodes.addAll(es);
        } finally {
            lock.unlock();
        }
    }

    public Collection<E> getAll() {
        lock.lock();
        try {
            return new ArrayList<>(nodes);
        } finally {
            lock.unlock();
        }
    }

    public Stream<E> stream() {return getAll().stream();}

    public Optional<Collection<E>> optional() {return Optional.of(getAll());}

    public int size() {return nodes.size();}

    public void clear() {
        lock.lock();
        try {
            nodes = new LinkedHashSet<>();
        } finally {
            lock.unlock();
        }
    }

    public Collection<E> clearAll() {
        lock.lock();
        try {
            LinkedHashSet<E> tmp = nodes;
            nodes = new LinkedHashSet<>();
            return tmp;
        } finally {
            lock.unlock();
        }
    }

    public List<List<E>> clearAll(int limit) {
        lock.lock();
        try {
            Collection<E> tmp = nodes;
            nodes = new LinkedHashSet<>();
            return ListOf.split(tmp, limit, null);
        } finally {
            lock.unlock();
        }
    }

}
