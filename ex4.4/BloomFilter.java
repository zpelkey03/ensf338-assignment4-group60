import java.util.BitSet;
import java.util.Random;

public class BloomFilter {
    private BitSet bitset;
    private int bitsetsize;
    private int refreshcount;
    private int insertions;
    private int[] hashseeds;
    private Random random;

    public BloomFilter(int bitsetsize, int refreshcount) {
        this.bitset = new BitSet(bitsetsize);
        this.bitsetsize = bitsetsize;
        this.refreshcount = refreshcount;
        this.insertions = 0;
        this.hashseeds = new int[]{31, 37, 41};
        this.random = new Random();
    }

    private int hash(String s, int seed) {
        int hash = seed;
        for (int i = 0; i < s.length(); i++) {
            hash = hash * 31 + s.charAt(i);
        }
        return Math.abs(hash);
    }

    public void record(String s) {
        for (int seed : hashseeds) {
            int index = hash(s, seed) % bitsetsize;
            bitset.set(index);
        }
        insertions++;
        if (insertions % refreshcount == 0) {
            bitset.clear();
            insertions = 0;
        }
    }

    public boolean lookup(String s) {
        for (int seed : hashseeds) {
            int index = hash(s, seed) % bitsetsize;
            if (!bitset.get(index)) {
                return false;
            }
        }
        return true;
    }
}
