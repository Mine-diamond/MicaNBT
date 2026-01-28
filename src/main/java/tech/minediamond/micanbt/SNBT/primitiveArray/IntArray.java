package tech.minediamond.micanbt.SNBT.primitiveArray;

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
        if (index >= size) throw new IndexOutOfBoundsException();
        return data[index];
    }

    public void set(int index, int value) {
        if (index >= size) throw new IndexOutOfBoundsException();
        data[index] = value;
    }

    public int size() {
        return size;
    }

    public void clear() {
        data = new int[INITIAL_CAPACITY];
        size = 0;
    }

    public int[] elements() {
        return data.clone();
    }

    public int[] toArray() {
        return Arrays.copyOf(data, size);
    }
}
