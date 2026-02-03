package tech.minediamond.micanbt.util.map;

import tech.minediamond.micanbt.tag.Tag;

import java.util.*;
import java.util.function.Function;

public class OrderedListMap<K, V extends Tag> implements Iterable<V> {
    private final Map<K, V> internalMap;
    private final List<K> keys;

    public OrderedListMap() {
        this.internalMap = new HashMap<>();
        this.keys = new ArrayList<>();
    }

    public OrderedListMap(Map<K, V> map) {
        this.internalMap = new HashMap<>(map);
        this.keys = new ArrayList<>(map.keySet());
    }

    public void put(K key, V value) {
        Objects.requireNonNull(value, "value");
        V old = internalMap.put(key, value);
        if (old == null) {
            keys.add(key);
        }
    }

    public void put(K key, V value, int index) {
        Objects.requireNonNull(value, "value");
        if (internalMap.containsKey(key)) {
            keys.remove(key);
        }
        keys.add(index, key);
        internalMap.put(key, value);
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public void putAll(OrderedListMap<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entries()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public V replaceAt(int index, K newKey, V newValue) {
        Objects.requireNonNull(newValue, "newValue");

        K oldKey = keys.get(index);

        if (!Objects.equals(oldKey, newKey) && internalMap.containsKey(newKey)) {
            throw new IllegalArgumentException("newKey already exists: " + newKey);
        }

        V oldValue = internalMap.get(oldKey);

        if (Objects.equals(oldKey, newKey)) {
            internalMap.put(oldKey, newValue);
            return oldValue;
        }
        
        keys.set(index, newKey);
        internalMap.remove(oldKey);
        internalMap.put(newKey, newValue);

        return oldValue;
    }

    public V replaceAt(K oldKey, K newKey, V newValue) {
        int index = keys.indexOf(oldKey);
        if (index < 0) {
            throw new NoSuchElementException("oldKey not found: " + oldKey);
        }
        return replaceAt(index, newKey, newValue);
    }

    public V get(K key) {
        return internalMap.get(key);
    }

    public V get(int index) {
        return internalMap.get(keys.get(index));
    }

    public V getOrDefault(K key, V defaultValue) {
        return internalMap.getOrDefault(key, defaultValue);
    }

    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        V existing = internalMap.get(key);
        if (existing != null) return existing;

        V computed = mappingFunction.apply(key);
        if (computed == null) {
            throw new NullPointerException("mappingFunction returned null");
        }
        put(key, computed);
        return computed;
    }

    public V remove(K key) {
        V old = internalMap.remove(key);
        if (old != null) {
            keys.remove(key);
        }
        return old;
    }

    public V remove(int index) {
        K key = keys.remove(index);
        return internalMap.remove(key);
    }

    public int size() {
        return keys.size();
    }

    public boolean isEmpty() {
        return keys.isEmpty();
    }

    public boolean containsKey(K key) {
        return internalMap.containsKey(key);
    }

    public void clear() {
        internalMap.clear();
        keys.clear();
    }


    public void swap(int index1, int index2) {
        Collections.swap(keys, index1, index2);
    }

    public void moveTo(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) return;
        K key = keys.remove(fromIndex);
        keys.add(toIndex, key);
    }

    public void moveTo(K key, int toIndex) {
        int fromIndex = keys.indexOf(key);
        if (fromIndex != -1) {
            moveTo(fromIndex, toIndex);
        }
    }

    public void sort(Comparator<? super K> comparator) {
        keys.sort(comparator);
    }

    public void sortByValue(Comparator<? super V> valueComparator) {
        keys.sort((k1, k2) -> valueComparator.compare(internalMap.get(k1), internalMap.get(k2)));
    }

    public int indexOf(K key) {
        return keys.indexOf(key);
    }

    public List<K> keyList() {
        return Collections.unmodifiableList(keys);
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ", "{", "}");
        for (K key : keys) {
            sj.add(key + "=" + internalMap.get(key));
        }
        return sj.toString();
    }

    @Override
    public Iterator<V> iterator() {
        return new ValueIterator();
    }

    private class ValueIterator implements Iterator<V> {
        private final Iterator<K> keyIterator = keys.iterator();
        private K lastKey = null;

        @Override
        public boolean hasNext() {
            return keyIterator.hasNext();
        }

        @Override
        public V next() {
            lastKey = keyIterator.next();
            return internalMap.get(lastKey);
        }

        @Override
        public void remove() {
            if (lastKey == null) {
                throw new IllegalStateException("next() has not been called");
            }
            internalMap.remove(lastKey);
            keyIterator.remove();
            lastKey = null;
        }
    }

    public Iterable<Map.Entry<K, V>> entries() {
        return () -> new Iterator<>() {
            private final Iterator<K> it = keys.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Map.Entry<K, V> next() {
                K k = it.next();
                return new AbstractMap.SimpleEntry<>(k, internalMap.get(k));
            }
        };
    }
}
