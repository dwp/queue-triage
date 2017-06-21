package uk.gov.dwp.queue.triage.core.dao.util;

import java.util.HashMap;

public class HashMapBuilder<K, V> {

    private final HashMap<K, V> hashMap = new HashMap<>();

    private HashMapBuilder() {}

    public static <K, V> HashMapBuilder<K, V> newHashMap() {
        return new HashMapBuilder<>();
    }

    public static <K, V> HashMapBuilder<K, V> newHashMap(Class<K> keyClass, Class<V> valueClass) {
        return new HashMapBuilder<>();
    }

    public HashMapBuilder<K, V> put(K key, V value) {
        hashMap.put(key, value);
        return this;
    }

    public HashMap<K, V> build() {
        return new HashMap<>(hashMap);
    }
}
