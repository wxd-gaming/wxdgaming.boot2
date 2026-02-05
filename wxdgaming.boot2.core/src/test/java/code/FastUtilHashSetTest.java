package code;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashSet;

public class FastUtilHashSetTest {

    static int count = 65535;

    @RepeatedTest(10)
    public void addObjectOpenHashSet() {
        ObjectOpenHashSet<String> objSets = new ObjectOpenHashSet<>();
        for (int i = 0; i < count; i++) {
            objSets.add("" + i);
        }
    }

    @RepeatedTest(10)
    public void addHashSet() {
        HashSet<String> objSets = new HashSet<>();
        for (int i = 0; i < count; i++) {
            objSets.add("" + i);
        }
    }

    @RepeatedTest(10)
    public void foreachObjectOpenHashSet() {
        ObjectOpenHashSet<String> objSets = new ObjectOpenHashSet<>();
        for (int i = 0; i < count; i++) {
            objSets.add("" + i);
        }
        long all = 0;
        for (String objSet : objSets) {
            all += Long.parseLong(objSet);
        }
    }

    @RepeatedTest(10)
    public void foreachHashSet() {
        HashSet<String> objSets = new HashSet<>();
        for (int i = 0; i < count; i++) {
            objSets.add("" + i);
        }
        long all = 0;
        for (String objSet : objSets) {
            all += Long.parseLong(objSet);
        }
    }

}
