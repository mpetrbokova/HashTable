import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChainingHashTable <K,V> implements DeletelessDictionary<K,V>{
    private List<Item<K,V>>[] table; // the table itself is an array of linked lists of items.
    private int size;
    private int buckets; // number of how many indexes are actually being used (not empty) in hashtable
    private static int[] primes = {11, 23, 47, 97, 197, 397, 797, 1597, 3203, 6421, 12853};
    private int primeIndex; // tracks which index we're on for the primes array

    public ChainingHashTable(){
        table = (LinkedList<Item<K,V>>[]) Array.newInstance(LinkedList.class, primes[0]);
        for(int i = 0; i < table.length; i++){
            table[i] = new LinkedList<>();
        }
        size = 0;
        buckets = 0;
        primeIndex = 0;
    }

    public boolean isEmpty(){
        return size == 0;
    }    

    public int size(){
        return size;
    }

    // Helper method that is used to rehash when the 
    // load factor is too high
    // Size of new table is chosen by nextPrimseSize() see details below
    private void rehash(){
        buckets = 0;
        int newSize = nextPrimeSize();
        List<Item<K,V>>[] largerTable = (LinkedList<Item<K,V>>[]) Array.newInstance(LinkedList.class, newSize);
        for(int i = 0; i < largerTable.length; i++) {
            largerTable[i] = new LinkedList<>();
    
        }
        for(int j = 0; j < table.length; j++){
            List<Item<K,V>> curr = table[j];
                    if(curr != null){
                        for(Item<K, V> item : curr){
                        int hashCode = Math.abs(item.key.hashCode()) % largerTable.length;
                        if(largerTable[hashCode].isEmpty()){
                            buckets++;
                        }
                        largerTable[hashCode].add(item);
                        }
                    }

        }
        table = largerTable;

    }

    // Helper method to choose next size of the table when rehashing.
    // First chooses next prime size if table but if those primes run out,
    // use 2^k + 1 such that it's at least twice the table size
    // since it acts as a prime number
    private int nextPrimeSize() {
        primeIndex++;
        if(primeIndex < primes.length - 1) {
            return primes[primeIndex];

        } else {
            int k = (int) Math.ceil(Math.log(size * 2) / Math.log(2));
            int candidateSize = (int) Math.pow(2, k) + 1;
            while(candidateSize < size * 2){
                k++;
                candidateSize = (int) Math.pow(2, k) + 1;
            }
            return candidateSize;
        }
    }

    // Inserts a new key-value pair using a hashcode on the key and
    // rehashes if load factor is too high
    public V insert(K key, V value){
        if(buckets > 0 && size / buckets >= 2){
            rehash();
        }
        int hashCode = Math.abs(key.hashCode()) % table.length;
        V answer = find(key);
        if(table[hashCode].isEmpty()){
            buckets++;
        }
        if(answer != null){
            for (Item<K, V> item : table[hashCode]) {
                if (item.key.equals(key)) {
                    item.value = value; 
                    return answer; 
                }
            }
       } else {
            table[hashCode].add(new Item<>(key, value));
            size++;
        }
        return answer;
    }

    // Finds the value associated with a given key or returns null
    // if table is empty or key does not exist in table
    public V find(K key){
        int hashCode = Math.abs(key.hashCode()) % table.length;
        if(table[hashCode].isEmpty()){
            return null;
        }
        for(Item<K, V> item : table[hashCode]){
            if(item.key.equals(key)){
                return item.value;
            }
        }
        return null;
       
    }
    

    // Returns true if the key is in the table, false otherwise
    public boolean contains(K key){
        return find(key) != null;
        
    }

    // Returns a list of all the keys in the table
    public List<K> getKeys(){
        List<K> keys = new ArrayList<>();
        for(int i = 0; i < table.length; i++) {
            if(table[i] != null){
                for (Item<K, V> item : table[i]) {
                    keys.add(item.key);
                }
                
            }
        }
        return keys;
    }

    // Returns a list of all the values in the table where the index of a value
    // in this list should be associated with the key in the pair in the key's list
    public List<V> getValues(){
        List<V> values = new ArrayList<>();
        for(int i = 0; i < table.length; i++) {
            if(table[i] != null){
                for (Item<K, V> item : table[i]) {
                    values.add(item.value);
                }
            
            }
        }
        return values;
    }

    public String toString(){
        String s = "{";
        s += table[0];
        for(int i = 1; i < table.length; i++){
            s += "," + table[i];
        }
        return s+"}";
    }

}
