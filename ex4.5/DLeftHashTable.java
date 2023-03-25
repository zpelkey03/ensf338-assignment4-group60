/*
DleftHashTable.java created for ex4.5
 */
import java.util.ArrayList;

public class DLeftHashTable {

    //a theoretical hash to use in this example
    private int hash(String key) {
        return Math.abs(key.hashCode()) % buckets;
    }

    // the private class for the pairs
    private class Pair {
        String key;
        int value;

        public Pair(String key, int value) {
            this.key = key;
            this.value = value;


            //private variables for the two tables and the number of buckets
    private ArrayList<ArrayList<Pair>> leftTable;
    private ArrayList<ArrayList<Pair>> rightTable;
    private int buckets;
    private int threshold;



    //constructor for the DLeftHashTable
    //takes in the number of buckets
    //and initializes the two tables
    //as well as the threshold
    public DLeftHashTable(int buckets) {
        this.buckets = buckets;
        this.threshold = (int) Math.floor(buckets / 2.0);
        this.leftTable = new ArrayList<ArrayList<Pair>>(buckets);
        this.rightTable = new ArrayList<ArrayList<Pair>>(buckets);
        for (int i = 0; i < buckets; i++) {
            leftTable.add(new ArrayList<Pair>());
            rightTable.add(new ArrayList<Pair>());
        }
    }

    //simple insertion method
    //takes in a key and a value
    //creates a pair object
    //inserts it into the right table if the right table is emptier
    public void insert(String key, int value) {
        Pair pair = new Pair(key, value);
        int hash = hash(key);
        if (hash < threshold) {
            leftTable.get(hash).add(pair);
        } else {
            rightTable.get(hash - threshold).add(pair);
        }
    }

    //a lookup method for a sepcific key in the hash table
    //returns the value if it is found
    public Integer lookup(String key) {
        int hash = hash(key);
        ArrayList<Pair> table;
        if (hash < threshold) {
            table = leftTable.get(hash);
        } else {
            table = rightTable.get(hash - threshold);
        }
        for (Pair pair : table) {
            if (pair.key.equals(key)) {
                return pair.value;
            }
        }
        return null;
    }

    //a
    private int hash(String key) {
        return Math.abs(key.hashCode()) % buckets;
    }

    private class Pair {
        String key;
        int value;

        public Pair(String key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}