package tech.minediamond.micanbt.tag;

import java.util.Arrays;

/**
 * TAG_Int_Array (ID: {@value #ID}).
 * <p>
 * Stores a {@code int[]} value.
 */
public class IntArrayTag extends Tag {
    public static final int ID = 11;
    private int[] value;

    /**
     * Creates a tag with the specified name.
     *
     * @param name The name of the tag.
     */
    public IntArrayTag(String name) {
        this(name, new int[0]);
    }

    /**
     * Creates a tag with the specified name.
     *
     * @param name  The name of the tag.
     * @param value The value of the tag.
     */
    public IntArrayTag(String name, int[] value) {
        super(name);
        this.value = value;
    }

    @Override
    public int[] getClonedValue() {
        return this.value.clone();
    }

    @Override
    public int[] getRawValue() {
        return this.value;
    }

    /**
     * Sets the value of this tag.
     *
     * @param value New value of this tag.
     */
    public void setValue(int[] value) {
        if (value == null) {
            return;
        }

        this.value = value.clone();
    }

    @Override
    public int getTagId() {
        return ID;
    }

    /**
     * Gets a value in this tag's array.
     *
     * @param index Index of the value.
     * @return The value at the given index.
     */
    public int getValue(int index) {
        return this.value[index];
    }

    /**
     * Sets a value in this tag's array.
     *
     * @param index Index of the value.
     * @param value Value to set.
     */
    public void setValue(int index, int value) {
        this.value[index] = value;
    }

    /**
     * Gets the length of this tag's array.
     *
     * @return This tag's array length.
     */
    public int length() {
        return this.value.length;
    }

    @Override
    public IntArrayTag copy() {
        return new IntArrayTag(this.getName(), this.getClonedValue());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && Arrays.equals(value, ((IntArrayTag) o).value);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Arrays.hashCode(value);
    }
}
