package tech.minediamond.micanbt.SNBT.primitiveArray;

import java.util.Arrays;

public class ByteArray {
    private byte[] data;
    private int size;
    private static final int INITIAL_CAPACITY = 10;

    public ByteArray() {
        data = new byte[INITIAL_CAPACITY];
        size = 0;
    }

    public void add(byte value) {
        if (size >= data.length) {
            // 扩容 1.5 倍
            data = Arrays.copyOf(data, size + (size >> 1));
        }
        data[size++] = value;
    }

    public byte get(int index) {
        if (index >= size || index < 0) throw new IndexOutOfBoundsException();
        return data[index];
    }

    public void set(int index, byte value) {
        if (index >= size || index < 0) throw new IndexOutOfBoundsException();
        data[index] = value;
    }

    public int size() {
        return size;
    }

    public void clear() {
        data = new byte[INITIAL_CAPACITY];
        size = 0;
    }

    public byte[] elements() {
        return data.clone();
    }

    public byte[] toArray() {
        return Arrays.copyOf(data, size);
    }
}
