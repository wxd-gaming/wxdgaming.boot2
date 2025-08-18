package wxdgaming.boot2.core.collection;

import lombok.Getter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.*;
import java.util.stream.Stream;

/**
 * 元素替换
 * <p> 非线程安全的
 * <p>后面加入的元素是会替换前面的元素
 * <p>请注意，替换规则是调用 在调用 hashcode equals 方法
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2022-02-16 10:46
 **/
@Getter
public class ConvertCollection<E> extends ObjectBase {

    private LinkedHashSet<E> nodes = new LinkedHashSet<>();

    /**
     * 元素替换
     * <p>后面加入的元素是会替换前面的元素
     * <p>请注意，替换规则是调用 在调用 hashcode equals 方法
     *
     * @param e 模型
     */
    public void add(E e) {
        nodes.remove(e);
        nodes.add(e);
    }

    /**
     * 元素替换
     * <p>后面加入的元素是会替换前面的元素
     * <p>请注意，替换规则是调用 在调用 hashcode equals 方法
     *
     * @param es 模型
     */
    public void addAll(Collection<E> es) {
        nodes.removeAll(es);
        nodes.addAll(es);
    }

    public Collection<E> getAll() {
        return new ArrayList<>(nodes);
    }

    public Stream<E> stream() {return getAll().stream();}

    public Optional<Collection<E>> optional() {return Optional.of(getAll());}

    public int size() {return nodes.size();}

    public void clear() {
        nodes = new LinkedHashSet<>();
    }

    public Collection<E> clearAll() {
        LinkedHashSet<E> tmp = nodes;
        nodes = new LinkedHashSet<>();
        return tmp;
    }

    public List<List<E>> clearAll(int limit) {
        Collection<E> tmp = nodes;
        nodes = new LinkedHashSet<>();
        return ListOf.split(tmp, limit, null);
    }

}
