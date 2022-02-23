package models;

import java.util.HashMap;
import java.util.Map;

public class HashTable<K1,K2,V> {

    private Map<HashPair<K1,K2>,V> table;

    public HashTable() {
        table = new HashMap<HashPair<K1,K2>,V>();
    }

    public V put(K1 key1, K2 key2, V value) {
        return table.put(new HashPair<K1,K2>(key1,key2), value);
    }

    public V get(K1 key1, K2 key2) {
        return table.get(new HashPair<K1,K2>(key1,key2));
    }
    
}
