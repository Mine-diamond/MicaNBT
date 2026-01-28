package tech.minediamond.micanbt.SNBT.primitiveArray;

import java.util.Arrays;

public class LongArray {
    private long[] data;
    private int size;
    private static final int INITIAL_CAPACITY = 10;

    public LongArray() {
        data = new long[INITIAL_CAPACITY];
        size = 0;
    }

    public void add(long value) {
        if (size >= data.length) {
            // 扩容 1.5 倍
            data = Arrays.copyOf(data, size + (size >> 1));
        }
        data[size++] = value;
    }

    public long get(int index) {
        if (index >= size || index < 0) throw new IndexOutOfBoundsException();
        return data[index];
    }

    public void set(int index, long value) {
        if (index >= size || index < 0) throw new IndexOutOfBoundsException();
        data[index] = value;
    }

    public int size() {
        return size;
    }

    public void clear() {
        data = new long[INITIAL_CAPACITY];
        size = 0;
    }

    public long[] elements() {
        return data.clone();
    }

    public long[] toArray() {
        return Arrays.copyOf(data, size);
    }
}
