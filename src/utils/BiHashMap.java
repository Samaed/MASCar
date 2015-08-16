package utils;

import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Bidirectional HashMap
 * @author Corentin
 * @param <K>
 * @param <V>
 */
public class BiHashMap<K,V> {
    
    private final HashMap<K,V> rightToLeft;
    private final HashMap<V,K> leftToRight;
    
    /**
     *
     */
    public BiHashMap() {
        rightToLeft = new HashMap<>();
        leftToRight = new HashMap<>();
    }
    
    /**
     *
     * @param key
     * @param value
     */
    public void put(K key, V value) {            
        rightToLeft.put(key, value);
        leftToRight.put(value, key);
    }
    
    /**
     *
     * @param key
     */
    public void removeKey(K key) {
        leftToRight.remove(rightToLeft.get(key));
        rightToLeft.remove(key);
    }
    
    /**
     *
     * @param value
     */
    public void removeValue(V value) {
        rightToLeft.remove(leftToRight.get(value));
        leftToRight.remove(value);
    }
    
    /**
     *
     * @param key
     * @return
     */
    public V getByKey(K key) {
        return rightToLeft.get(key);
    }
    
    /**
     *
     * @param key
     * @return
     */
    public boolean containsKey(K key) {
        return rightToLeft.containsKey(key);
    }
    
    /**
     *
     * @param value
     * @return
     */
    public K getByValue(V value) {
        return leftToRight.get(value);
    }
    
    /**
     *
     * @param value
     * @return
     */
    public boolean containsValue(V value) {
        return leftToRight.containsKey(value);
    }
    
    /**
     *
     * @param action
     */
    public void foreachKey(BiConsumer<? super K, ? super V> action) {
        rightToLeft.forEach(action);
    }
    
    /**
     *
     * @param action
     */
    public void foreachValue(BiConsumer<? super V, ? super K> action) {
        leftToRight.forEach(action);
    }
    
}
