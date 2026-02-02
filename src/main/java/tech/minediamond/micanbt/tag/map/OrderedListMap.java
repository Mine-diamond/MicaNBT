package tech.minediamond.micanbt.tag.map;

import tech.minediamond.micanbt.tag.Tag;

import java.util.*;

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
        if (!internalMap.containsKey(key)) {
            keys.add(key);
        }
        internalMap.put(key, value);
    }

    public void put(K key, V value, int index) {
        if (!internalMap.containsKey(key)) {
            keys.add(index, key);
        }
        internalMap.put(key, value);
    }

    public void putAll(Map<K, V> map) {
        map.forEach(this::put);
    }

    public void putAll(OrderedListMap<K, V> map) {
        map.entries().forEach((kvEntry) -> put(kvEntry.getKey(), kvEntry.getValue()));
    }

    public V get(K key) {
        return internalMap.get(key);
    }

    public V remove(K key) {
        keys.remove(key);
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

    // --- 顺序控制操作 ---

    public void swap(int index1, int index2) {
        Collections.swap(keys, index1, index2);
    }

    public void moveTo(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= keys.size() || toIndex < 0 || toIndex >= keys.size()) {
            throw new IndexOutOfBoundsException();
        }
        K key = keys.remove(fromIndex);
        keys.add(toIndex, key);
    }

    public void moveTo(K key, int toIndex) {
        if (toIndex < 0 || toIndex >= keys.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (containsKey(key)) {
            moveTo(indexOf(key), toIndex);
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

    public List<Map.Entry<K, V>> entryList() {
        List<Map.Entry<K, V>> list = new ArrayList<>();
        for (K key : keys) {
            list.add(new AbstractMap.SimpleEntry<>(key, internalMap.get(key)));
        }
        return list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < keys.size(); i++) {
            K key = keys.get(i);
            sb.append(key).append("=").append(internalMap.get(key));
            if (i < keys.size() - 1) sb.append(", ");
        }
        return sb.append("}").toString();
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
