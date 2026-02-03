package tech.minediamond.micanbt.util.primitivearray;

import java.util.Arrays;

public class IntArray {
    private int[] data;
    private int size;
    private static final int INITIAL_CAPACITY = 10;

    public IntArray() {
        data = new int[INITIAL_CAPACITY];
        size = 0;
    }

    public void add(int value) {
        if (size >= data.length) {
            data = Arrays.copyOf(data, size + (size >> 1));
        }
        data[size++] = value;
    }

    public int get(int index) {
        if (index >= size || index < 0) throw new IndexOutOfBoundsException();
        return data[index];
    }

    public void set(int index, int value) {
        if (index >= size || index < 0) throw new IndexOutOfBoundsException();
        data[index] = value;
    }

    public int size() {
        return size;
    }

    public void clear() {
        size = 0;
    }

    public int[] toArray() {
        return Arrays.copyOf(data, size);
    }
}
